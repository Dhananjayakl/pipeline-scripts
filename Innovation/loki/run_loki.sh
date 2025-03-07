
docker run -it -d --name=loki \
  -p 3100:3100 \
  -v /loki/loki-config.yaml:/etc/loki/loki-config.yaml \
  -v /loki/data:/tmp/loki \
  grafana/loki:latest \
  -config.file=/etc/loki/loki-config.yaml \
  sh
