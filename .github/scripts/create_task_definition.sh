#!/bin/bash

task_def=$(cat <<EOF
{
  "family": "orders-service-task",
  "containerDefinitions": [{
    "name": "orders-service",
    "image": "${ECR_REGISTRY}/${ECR_REPOSITORY}:${IMAGE_TAG}",
    "memory": 512,
    "cpu": 256,
    "essential": true,
    "portMappings": [{
      "containerPort": 80,
      "hostPort": 80
    }]
  }]
}
EOF
)

echo "$task_def" > task-def.json
aws ecs register-task-definition --cli-input-json file://task-def.json
task_revision=$(aws ecs describe-task-definition --task-definition orders-service-task | jq -r '.taskDefinition.revision')
echo "::set-output name=revision::$task_revision"
