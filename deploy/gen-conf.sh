# envsubst < docker-compose.yml > docker-compose.gen.yml
export $(grep -v '^#' .env | xargs) && envsubst < docker-compose.yml.template > docker-compose.gen.yml