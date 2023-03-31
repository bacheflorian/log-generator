# log-generator
Repository containing tools for generating semi-randomized computer telemetry (or log data)

# team-members
- Andy Wu
- Chinwe Ajieh
- Florian Bache
- Ling Lee
- David Zarinski

# deployment
### Overview<br/>
The full stack application is deployed via AWS ECS with self-managed EC2 instances using an automated Jenkins pipeline. Below is an overview of the steps required to do so:  
- Dockerfiles are used to create docker images of both the frontend and backend. These images are uploaded to AWS Elastic Container Repository (ECR)
- An Elastic Container Service (ECS) cluster is created and utilized as a container orchestration and management tool
- The ECS deploys both the frontend and backend images to containers which run on two different EC2 instances

### Implementation<br/>
Configure AWS
- ECR
  - Create lg-backend repository
  - Create lg-frontend repository
- ECS
  - Create Cluster
    - EC2 Linux + Networking
      - Cluster name: ad1EcsCluster
      - EC2 instance type: t2.micro
      - VPC (create new)
        - CIDR: 10.0.0.0/16
        - Subnet 1: 10.0.0.0/24
        - Subnet 2: 10.0.1.0/24
      - Security Group (create new)
      - Container instance IAM role
        - Create new role
  - Task definitions
    - Create ad1-back task definition
      - Requires compatibilities
        - EC2
      - Task execution IAM role (create new)
      - Task size 
        - Task memory: 800 MiB
        - Task cpu: 1024
      - Add container
        - Container name: ad1-back
        - Image: copy lg-backend image URI from ECR (latest)
        - Port mappings: 8080:8080
    - Create ad1-front task definition
      - Requires compatibilities
        - EC2
      - Task execution IAM role (create new)
      - Task size 
        - task memory: 800 MiB
        - task cpu: 1024
      - Add container
        - Container name: ad1-front
        - Image: copy lg-frontend image URI from ECR (latest)
        - Port mappings: 80:3000
  - Services
    - Navigate to created cluster. Under Services tab click Create
        - Backend service
          - Launch type: EC2
          - Task Definition: ad1-back
          - Revision: latest
          - Service name: ad1-back
          - Number of tasks: 0
        - Frontend service
          - Launch type: EC2
          - Task Definition: ad1-front
          - Revision: latest
          - Service name: ad1-front
          - Number of tasks: 0
- EC2
  - Elastic IPs
    - backend
      - Allocate Elastic IP address
      - name backend
    - frontend
      - Allocate Elastic IP address
      - name frontend 
  - Security Groups
    - Create security group
      - Security group name: ad1
      - VPC: Same as created in Cluster above
      - Inbound rules:
        - IP version Type Protocol Port range Source Description
        - All traffic	All	All	default	–
        - IPv4	HTTPS	TCP	443	0.0.0.0/0	–
        - IPv4	Custom TCP	TCP	8080	0.0.0.0/0	–
        - IPv6	HTTP	TCP	80	::/0	–
        - IPv6	Custom TCP	TCP	8080	::/0	–
        - IPv4	HTTP	TCP	80	0.0.0.0/0	–
        - IPv6	HTTPS	TCP	443	::/0
  - EC2 instances
    - Create frontend instance
      - Application and OS Images: Ubuntu
      - Instance type: t2.micro
      - Network settings
        - Network: VPC created in cluster above
        - Subnet: Subnet created above
        - Select existing Security group: Security group created above
      - Name instance frontend after creation
      - Navigate to Elastic IPs - associate an IP with the EC2 instance
    - Create backend instance
      - Application and OS Images: Ubuntu
      - Instance type: t2.micro
      - Network settings
        - Network: VPC created in cluster above
        - Subnet: Subnet created above
        - Select existing Security group: Security group created above
      - Name instance frontend
      - Navigate to Elastic IPs - associate an IP with the EC2 instance

Jenkins pipeline
