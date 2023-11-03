    package com.example.demo.service;

    import io.fabric8.kubernetes.api.model.Node;
    import io.fabric8.kubernetes.api.model.NodeList;
    import io.fabric8.kubernetes.api.model.Pod;
    import io.fabric8.kubernetes.api.model.PodList;
    import io.fabric8.kubernetes.api.model.batch.v1.Job;
    import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
    import io.fabric8.kubernetes.client.Config;
    import io.fabric8.kubernetes.client.ConfigBuilder;
    import io.fabric8.kubernetes.client.DefaultKubernetesClient;
    import io.fabric8.kubernetes.client.KubernetesClient;
    import io.fabric8.kubernetes.client.KubernetesClientException;
    import org.springframework.stereotype.Service;
    import org.springframework.util.StreamUtils;

    import java.io.IOException;
    import java.io.InputStream;
    import java.nio.charset.StandardCharsets;
    import java.io.ByteArrayInputStream;

    import java.util.ArrayList;
    import java.util.List;


    @Service
    public class KubernetesService {

        public List<String> deployJobFromYaml(String apiServer, String token) {
            System.out.println("Start KCS2 Scanner\n");
            List<String> logMessage = new ArrayList<>();

            Config config = new ConfigBuilder()
                    .withMasterUrl(apiServer)
                    .withOauthToken(token)
                    .withTrustCerts(true)
                    .build();

            try (KubernetesClient kubernetesClient = new DefaultKubernetesClient(config)) {
                try {

                    NodeList nodeList = kubernetesClient.nodes().list();

                    System.out.println("Successfully call Nodelist");

                    for (Node node : nodeList.getItems()) {
                        String nodeName = node.getMetadata().getName();
                        String yamlTemplate = loadYamlTemplateFromFile();
                        String yamlContent = yamlTemplate.replace("${nodeName}", nodeName);
                        InputStream inputStream = new ByteArrayInputStream(yamlContent.getBytes());
                        
                        System.out.println("Here is yaml file's content\n"+yamlContent);

                        kubernetesClient.load(inputStream).create();

                        System.out.println("==============   " + nodeName+"Job created successfully.   ==============");

                        logMessage.add("==============   "+nodeName+" Job information.   ==============");
                        
                        inputStream.close();

                        waitForJobCompletion(kubernetesClient, nodeName+"kubebench", logMessage);

                        PodList podList = kubernetesClient.pods().inNamespace("default").list();
                        String log = null;
                        for (Pod pod : podList.getItems()) {
                            String podName = pod.getMetadata().getName();
                            if (podName.startsWith(nodeName+"kubebench")) {

                                log = kubernetesClient.pods().inNamespace("default").withName(podName).getLog();

                                System.out.println(log);

                                logMessage.add(log);

                                break;
                            }
                        }
                        deleteJob(kubernetesClient,nodeName+"kubebench",logMessage);
                    }
                } catch (IOException e) {
                    logMessage.add("Given TOKEN's authority is lack. Please check following authority");
                    logMessage.add("I want to do");
                    logMessage.add("· Read your node list");
                    logMessage.add("· Read your pod list");
                    logMessage.add("· Create JOB in your nodes");
                    logMessage.add("· Delete JOB in your nodes");
                    logMessage.add("· GET JOB's data in your nodes");
                    throw new RuntimeException(e);
                }
            } catch (KubernetesClientException e) {
                logMessage.add("Your URL or TOKEN is incorrect. Please check again");
                e.printStackTrace();
                // Handle Kubernetes client errors
            }
            return logMessage;
        }

        private String loadYamlTemplateFromFile() throws IOException {
            try (InputStream inputStream = getClass().getResourceAsStream("/application.yml")) {
                byte[] bytes = StreamUtils.copyToByteArray(inputStream);
                return new String(bytes, StandardCharsets.UTF_8);
            }
        }
        private void waitForJobCompletion(KubernetesClient kubernetesClient, String jobName, List<String> logMessage) {
            long timeoutMillis = 5 * 60 * 1000; // 최대 대기 시간 (예: 10분)
            long pollingIntervalMillis = 1000; // 상태 확인 간격 (예: 1초)

            long startTime = System.currentTimeMillis();
            boolean jobCompleted = false;

            while (!jobCompleted && System.currentTimeMillis() - startTime < timeoutMillis) {
                Job job = kubernetesClient.batch().v1().jobs().inNamespace("default").withName(jobName).get();
                if (job != null) {
                    JobStatus jobStatus = job.getStatus();
                    if (jobStatus != null && jobStatus.getSucceeded() != null && jobStatus.getSucceeded() > 0) {
                        jobCompleted = true;
                    }
                }

                if (!jobCompleted) {
                    try {
                        Thread.sleep(pollingIntervalMillis); // 일정 간격으로 상태 확인
                    } catch (InterruptedException e) {
                        System.out.println("Time out error with "+ jobName);
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (jobCompleted) {
                System.out.println("Job completed successfully.");
            } else {
                System.err.println("Timeout waiting for Job completion.");
                logMessage.add("Time out error with "+ jobName);
            }
        }

        private void deleteJob(KubernetesClient kubernetesClient, String jobName, List<String> logMessage) {
            try {
                kubernetesClient.batch().v1().jobs().inNamespace("default").withName(jobName).delete();

                System.out.println("Job deleted successfully.");
            } catch (KubernetesClientException e) {
                e.printStackTrace();
                System.err.println("Error while deleting Job.");
                logMessage.add("Error while deleting Job.");
            }
        }
    }

