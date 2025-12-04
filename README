Quarkus (Java)-based reproducer for SeaweedFS S3 Object Tagging bug.

`main` branch deploys a `localstack` container for S3 as a sanity test.

`seaweed` branch deploys a SeaweedFS-based container with tiny S3 config layer to test a simple workflow:

1. upload an object file to S3 with a metadata Tag
2. download file content and assert it's as expected
3. download file Tagging and assert it's as expected
4. delete file

Check out `seaweed` branch and run `./test.bash 4.00` to run this test against SeaweedFS 4.00, which should pass. Then try with `./test.bash 4.01`, which is observed to fail. Then try with `QUARKUS_S3_CHECKSUM_VALIDATION=false ./test.bash dev`, `QUARKUS_S3_CHUNKED_ENCODING=false ./test.bash dev`, `QUARKUS_S3_CHECKSUM_VALIDATION=true QUARKUS_S3_CHUNKED_ENCODING=false ./test.bash dev`, etc.

Testing against current SeaweedFS upstream can be done via `SOURCE_BUILD=true ./test.bash`. A particular upstream branch can be built and tested with `SOURCE_BUILD=true ./test.bash name-of-branch`.

The test uses the Object Tag API by default. `MODE=meta ./test.bash` can be used to run the test using the Object Metadata API instead.

If the test fails with output like below, run it again. This seems to be due to resource constraints on the testcontainer running SeaweedFS:

```
[ERROR]   StorageResourceTest.test:31 1 expectation failed.
Response body doesn't match expectation.
Expected: "some file content\n"
  Actual: {"details":"Error id 59249cf6-0d73-4370-acc2-129e41f7c2e6-1, software.amazon.awssdk.services.s3.model.S3Exception: We encountered an internal error, please try again. (Service: S3, Status Code: 500, Request ID: 1764879105902222709) (SDK Attempt Count: 4)","stack":"software.amazon.awssdk.services.s3.model.S3Exception: We encountered an internal error, please try again. (Service: S3, Status Code: 500, Request ID: 1764879105902222709) (SDK Attempt Count: 4)\n\tat software.amazon.awssdk.services.s3.model.S3Exception$BuilderImpl.build(S3Exception.java:113)\n\tat software.amazon.awssdk.services.s3.model.S3Exception$BuilderImpl.build(S3Exception.java:61)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.utils.RetryableStageHelper.retryPolicyDisallowedRetryException(RetryableStageHelper.java:168)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.RetryableStage.execute(RetryableStage.java:73)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.RetryableStage.execute(RetryableStage.java:36)\n\tat software.amazon.awssdk.core.internal.http.pipeline.RequestPipelineBuilder$ComposingRequestPipelineStage.execute(RequestPipelineBuilder.java:206)\n\tat software.amazon.awssdk.core.internal.http.StreamManagingStage.execute(StreamManagingStage.java:53)\n\tat software.amazon.awssdk.core.internal.http.StreamManagingStage.execute(StreamManagingStage.java:35)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallTimeoutTrackingStage.executeWithTimer(ApiCallTimeoutTrackingStage.java:82)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallTimeoutTrackingStage.execute(ApiCallTimeoutTrackingStage.java:62)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallTimeoutTrackingStage.execute(ApiCallTimeoutTrackingStage.java:43)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallMetricCollectionStage.execute(ApiCallMetricCollectionStage.java:50)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallMetricCollectionStage.execute(ApiCallMetricCollectionStage.java:32)\n\tat software.amazon.awssdk.core.internal.http.pipeline.RequestPipelineBuilder$ComposingRequestPipelineStage.execute(RequestPipelineBuilder.java:206)\n\tat software.amazon.awssdk.core.internal.http.pipeline.RequestPipelineBuilder$ComposingRequestPipelineStage.execute(RequestPipelineBuilder.java:206)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.ExecutionFailureExceptionReportingStage.execute(ExecutionFailureExceptionReportingStage.java:37)\n\tat software.amazon.awssdk.core.internal.http.pipeline.stages.ExecutionFailureExceptionReportingStage.execute(ExecutionFailureExceptionReportingStage.java:26)\n\tat software.amazon.awssdk.core.internal.http.AmazonSyncHttpClient$RequestExecutionBuilderImpl.execute(AmazonSyncHttpClient.java:210)\n\tat software.amazon.awssdk.core.internal.handler.BaseSyncClientHandler.invoke(BaseSyncClientHandler.java:103)\n\tat software.amazon.awssdk.core.internal.handler.BaseSyncClientHandler.doExecute(BaseSyncClientHandler.java:173)\n\tat software.amazon.awssdk.core.internal.handler.BaseSyncClientHandler.lambda$execute$0(BaseSyncClientHandler.java:66)\n\tat software.amazon.awssdk.core.internal.handler.BaseSyncClientHandler.measureApiCallSuccess(BaseSyncClientHandler.java:182)\n\tat software.amazon.awssdk.core.internal.handler.BaseSyncClientHandler.execute(BaseSyncClientHandler.java:60)\n\tat software.amazon.awssdk.core.client.handler.SdkSyncClientHandler.execute(SdkSyncClientHandler.java:52)\n\tat software.amazon.awssdk.awscore.client.handler.AwsSyncClientHandler.execute(AwsSyncClientHandler.java:60)\n\tat software.amazon.awssdk.services.s3.DefaultS3Client.getObject(DefaultS3Client.java:6416)\n\tat software.amazon.awssdk.services.s3.S3Client.getObject(S3Client.java:11199)\n\tat software.amazon.awssdk.services.s3.S3Client_AJJRdIXhrwPQ5pyWIbuMF8Mf2II_Synthetic_ClientProxy.getObject(Unknown Source)\n\tat org.acme.StorageResource.download(StorageResource.java:63)\n\tat org.acme.StorageResource$quarkusrestinvoker$download_7c0c40ad3aa3eb2045c1031c8f5c26946ad7bc91.invoke(Unknown Source)\n\tat org.jboss.resteasy.reactive.server.handlers.InvocationHandler.handle(InvocationHandler.java:29)\n\tat io.quarkus.resteasy.reactive.server.runtime.QuarkusResteasyReactiveRequestContext.invokeHandler(QuarkusResteasyReactiveRequestContext.java:183)\n\tat org.jboss.resteasy.reactive.common.core.AbstractResteasyReactiveContext.run(AbstractResteasyReactiveContext.java:147)\n\tat io.quarkus.vertx.core.runtime.VertxCoreRecorder$15.runWith(VertxCoreRecorder.java:645)\n\tat org.jboss.threads.EnhancedQueueExecutor$Task.doRunWith(EnhancedQueueExecutor.java:2651)\n\tat org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2630)\n\tat org.jboss.threads.EnhancedQueueExecutor.runThreadBody(EnhancedQueueExecutor.java:1622)\n\tat org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1589)\n\tat org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:11)\n\tat org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:11)\n\tat io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)\n\tat java.base/java.lang.Thread.run(Thread.java:1583)\n\tSuppressed: software.amazon.awssdk.core.exception.SdkClientException: Request attempt 1 failure: We encountered an internal error, please try again. (Service: S3, Status Code: 500, Request ID: 1764879098094723674)\n\tSuppressed: software.amazon.awssdk.core.exception.SdkClientException: Request attempt 2 failure: We encountered an internal error, please try again. (Service: S3, Status Code: 500, Request ID: 1764879100686197631)\n\tSuppressed: software.amazon.awssdk.core.exception.SdkClientException: Request attempt 3 failure: We encountered an internal error, please try again. (Service: S3, Status Code: 500, Request ID: 1764879103321830967)"}
```

The "expected" failure case looks like:

```
[ERROR] Failures: 
[ERROR]   StorageResourceTest.test:31 1 expectation failed.
Response body doesn't match expectation.
Expected: "some file content\n"
  Actual: 12;chunk-signature=a2bed7653f8288cc6db72899414f2aa611923a356491d594a66e372df0860066
some file content

0;chunk-signature=a4ff04ec1842c3ee54dbe884858862bd438f5095984df6ff261234c1888244e8
x-amz-checksum-crc32:cVmzJg==
x-amz-trailer-signature:d1f4ad539865f683b464a8ce9da211935f6af2d1011f91029db363a312657310
```
