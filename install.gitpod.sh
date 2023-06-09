echo "->Welcome to the installation script for Gitpod"

echo "->Ensuring .bashrc exists and is writable"
touch ~/.bashrc

if [ -x "$(command -v docker)" ]; then
    echo "->Docker already available"
    echo ""
else
    echo "->Installing Docker"
    echo ""
    #  Install Docker
    curl -fsSL https://get.docker.com -o get-docker.sh
fi

if [ -x "$(command -v docker)" ]; then
    echo "->Docker Compose already available"
    echo ""
else
    echo "->Installing docker-compose"
    echo ""
    sudo curl -L "https://github.com/docker/compose/releases/download/1.26.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
fi

echo "->Create springboot-crud build"
sudo apt-get update -y
sudo apt-get install maven -y
mvn clean install -DskipTests

echo "->Start mysqldb"
docker-compose up -d mysqldb

echo "->Start springboot-crud application"
docker-compose up -d ttapp