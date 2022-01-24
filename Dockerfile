FROM solr:8.11.1

# copy fix into Solr
COPY target/classes/org /opt/solr/server/solr-webapp/webapp/WEB-INF/classes/org
#COPY target/solr-external-file-field-fixes-0-SNAPSHOT.jar /opt/solr/server/lib/solr-external-file-field-fixes.jar
