provider "aws" {
  region     = "us-east-1"
}

resource "aws_instance" "instance_311" {
  ami		= "ami-0ac019f4fcb7cb7e6"
  instance_type = "m5.large"
  key_name = "aws_key"
  tags {
    Name = "instance_311"
  }

  root_block_device {
    volume_type           = "gp2"
    volume_size           = "30"
    delete_on_termination = "true"
  }

#  ebs_block_device {
#    device_name = "/dev/xvda"
#    volume_type = "gp2"
#    volume_size = "30" 
#    delete_on_termination = "true"
#  }

}
