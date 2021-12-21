FROM solr:8.11.1

COPY target/classes/org /opt/solr/server/solr-webapp/webapp/WEB-INF/classes/org
