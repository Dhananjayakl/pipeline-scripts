docker run -it -d --name=promtail \
  --cap-add SYS_ADMIN \
  --user=root \
  -p 9080:9080 \
  --volume=/var/lib/docker/volumes/RNDSTG-ORACLE/_data/logs/ProgrecApps/application.log:/var/log/RNDSTG-ORACLE/application.log:ro \
  --volume=/var/lib/docker/volumes/RNDQA-ORACLE/_data/logs/ProgrecApps/application.log:/var/log/RNDQA-ORACLE/application.log:ro \
  --volume=/var/lib/docker/volumes/INNOVATION/_data/logs/ProgrecApps/application.log:/var/log/INNOVATION/application.log:ro \
  --volume=/promtail/promtail-config.yaml:/etc/promtail/promtail-config.yaml:ro \
  --volume=/var/log:/var/log/host_229:ro \
  --volume=/var/lib/docker/volumes/INNOVATION-5/_data/logs/ProgrecApps/application.log:/var/log/INNOVATION-5/application.log:ro \
  --volume=/var/lib/docker/containers:/var/lib/docker/containers:ro \
  --link loki \
  grafana/promtail:latest \
  -config.file=/etc/promtail/promtail-config.yaml
