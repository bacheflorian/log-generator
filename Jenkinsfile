pipeline {
    agent any
    environment {
        registryFront = "public.ecr.aws/p1e0j1r1/lg-frontend"
        registryBack = "public.ecr.aws/p1e0j1r1/lg-backend"
    }
    stages {
        stage('Checkout GitHub repository') {
            steps {
                git branch: 'prod',
                    url: 'https://github.com/andymbwu/log-generator'
            }
        }
        stage('Building frontend image') {
          steps{
            dir('frontend') {
              script {
                dockerImage = docker.build registryFront
              }
            }
          }
        }
        stage('Pushing frontend image to ECR') {
         steps{
             withCredentials ([[
                 $class: 'AmazonWebServicesCredentialsBinding',
                 credentialsId: 'jenkins-aws-admin',
                 accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                 secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
                    sh 'aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/p1e0j1r1'
                    sh "docker push public.ecr.aws/p1e0j1r1/lg-frontend:latest"
                }
            }
        }
        stage('Building backend image') {
          steps{
            dir('backend') {
              script {
                dockerImage = docker.build registryBack
              }
            }
          }
        }
        stage('Pushing backend image to ECR') {
         steps{
             withCredentials ([[
                 $class: 'AmazonWebServicesCredentialsBinding',
                 credentialsId: 'jenkins-aws-admin',
                 accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                 secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
                    sh 'aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin public.ecr.aws/p1e0j1r1'
                    sh "docker push public.ecr.aws/p1e0j1r1/lg-backend:latest"
                }
            }
        }
        stage('Deploy backend') {
          steps {
            withCredentials([[
              $class: 'AmazonWebServicesCredentialsBinding',
               credentialsId: 'jenkins-aws-admin',
               accessKeyVariable: 'AWS_ACCESS_KEY_ID',
               secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
                  sh 'aws ec2 start-instances --instance-ids i-0605937bc6c04b756 --region us-west-2'
                  sleep time: 60, unit: 'SECONDS'
                  sh "aws ecs update-service --cluster ad1EcsCluster --service ad1-back --desired-count 1 --force-new-deployment --region us-west-2"
                }
            }
        }
        stage('Deploy frontend') {
          steps {
            withCredentials([[
              $class: 'AmazonWebServicesCredentialsBinding',
               credentialsId: 'jenkins-aws-admin',
               accessKeyVariable: 'AWS_ACCESS_KEY_ID',
               secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
                  sh 'aws ec2 start-instances --instance-ids i-07991c44637ace785 --region us-west-2'
                  sleep time: 60, unit: 'SECONDS'
                  sh "aws ecs update-service --cluster ad1EcsCluster --service ad1-front --desired-count 1 --force-new-deployment --region us-west-2"
                }
            }
        }
    }
}