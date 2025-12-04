package org.acme;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestPath;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

@Path("/storage")
public class StorageResource {

    @ConfigProperty(name="storage-resource.bucket") String bucket;
    @ConfigProperty(name="mode", defaultValue="tag") String mode;
    @Inject S3Client s3;
    @Inject Logger logger;

    void onStart(@Observes StartupEvent evt) {
        HeadBucketRequest.Builder headBucket = HeadBucketRequest.builder().bucket(bucket);
        boolean exists = false;
        try {
            exists = s3.headBucket(headBucket.build()).sdkHttpResponse().isSuccessful();
        } catch (S3Exception e) {
            logger.debug(e);
        }
        if (!exists) {
            CreateBucketRequest.Builder createBucket = CreateBucketRequest.builder().bucket(bucket);
            s3.createBucket(createBucket.build());
            logger.infov("Created S3 bucket \"{0}\"", bucket);
        }
        logger.infov("S3 bucket \"{0}\" ready", bucket);
        logger.infov("Using mode: {0}", mode);
    }

    @POST
    @Path("{id}")
    public void upload(@RestPath String id, File body) {
        logger.infov("Uploading {0}/{1}", bucket, id);
        switch (mode) {
            case "tag":
                s3.putObject(PutObjectRequest.builder().bucket(bucket).key(id).tagging(Tagging.builder().tagSet(Tag.builder().key("hello").value("world").build()).build()).build(), body.toPath());
                break;
            case "meta":
                s3.putObject(PutObjectRequest.builder().bucket(bucket).key(id).metadata(Map.of("hello", "world")).build(), body.toPath());
                break;
            default:
                throw new IllegalArgumentException(mode);
        }
    }

    @GET
    @Path("{id}")
    public InputStream download(@RestPath String id) {
        logger.infov("Downloading {0}/{1}", bucket, id);
        return s3.getObject(GetObjectRequest.builder().bucket(bucket).key(id).build());
    }

    @GET
    @Path("{id}/meta")
    public Map<String, String> downloadMeta(@RestPath String id) {
        logger.infov("Downloading metadata {0}/{1}", bucket, id);
        switch (mode) {
            case "tag":
                return s3.getObjectTagging(GetObjectTaggingRequest.builder().bucket(bucket).key(id).build()).tagSet().stream().collect(Collectors.toMap(Tag::key, Tag::value));
            case "meta":
                return s3.headObject(HeadObjectRequest.builder().bucket(bucket).key(id).build()).metadata();
            default:
                throw new IllegalArgumentException(mode);
        }
    }

    @DELETE
    @Path("{id}")
    public void delete(@RestPath String id) {
        s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(id).build());
    }
}
