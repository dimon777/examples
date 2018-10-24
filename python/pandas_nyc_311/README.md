# Pandas demo on NYC 311 datasets
This demonstraits:
- Creating AWS instance with Terraform
- Using jupyter notebook to execute simple analytic pipeline on 311 dataset


### Step 1: Provision AWS instance with Terraform (this is done on local machine)

- Pick Ubuntu instance at [Ec2 locator](https://cloud-images.ubuntu.com/locator/ec2/)
- Go to: [IAM home](https://console.aws.amazon.com/iam/home)
- Create security credentials:
```
Users -> select your username -> Security credentials -> Create Access Key -> Record access key id and secret_access_key
```
- Add yourself to Admin group:
```
Users -> select your username -> Add permissions -> Add to AdministratorAccess group
```

- Edit files to add required keys and secrets:
```
~/.aws/credentials
[default]
aws_access_key_id = YOUR_KEY
aws_secret_access_key = YOUR_SECRET

~/.aws/config
[default]
region=us-east-1
```

- Clone [this repo](https://github.com/dimon777/pandas_nyc_311)
```
git clone https://github.com/dimon777/pandas_nyc_311
```

- Install [Terraform](https://www.terraform.io/intro/getting-started/install.html)

- Create AWS instance and security group with Terraform
```
cd terraform
terraform init
terraform apply
```

- In [AWS Console](https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#Instances:sort=instanceId) add instance into new Security Group (311_access) 
```
-> Instance -> Actions -> Netoworking -> Change Security Groups -> 311_access
```

- In [AWS Console](https://console.aws.amazon.com/ec2/v2/home?region=us-east-1#KeyPairs:sort=keyName) create Key Pair
```
-> Create Key Pair -> Name: aws_key.pem -> Note location of the pem file: `aws_key.pem`
```

- Verify SSH connection to your instance
```
ssh -i "aws_key.pem" ubuntu@<instance public IP>
```


### Step 2: Execute simple data pipeline (this is done on AWS instance)

- Clone [this repo](https://github.com/dimon777/pandas_nyc_311)
```
git clone https://github.com/dimon777/pandas_nyc_311
```

### Requirements:
pip3 install -r requirements.txt # --user
