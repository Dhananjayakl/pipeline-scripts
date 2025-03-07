docker run -d   --name=postgres-exporter-dev \
   -p 9187:9187 \
   -e DATA_SOURCE_NAME="postgresql://hrms:hrms@192.168.1.211:5432/hrmsdb?sslmode=disable" \
   quay.io/prometheuscommunity/postgres-exporter
