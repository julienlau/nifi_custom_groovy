// Form a GELF message for Graylog in the content flowfile.
// Message is formed by the attributes of the incoming flowfile.
// Default values provided to ensure a valid GELF format.
@Grab(group='commons-io', module='commons-io', version='2.6')
@Grab(group='charset', module='charset', version='1.2.1')
//@Grab(group='org.codehaus.groovy', module='groovy-json', version='2.4.5')
import org.apache.commons.io.IOUtils
import java.nio.charset.StandardCharsets
import groovy.json.*

flowFile = session.get()
if (!flowFile) return
try {
    context.properties.findAll {k,v -> k.dynamic}.each {k,v -> log.info(k.name)}
    log.info('CustomNifiGELF create payload')
    def jsonSlurper = new JsonSlurper()
    // 3 Mandatory Keys: short_message, host, facility
    def myGelf = jsonSlurper.parseText('''{
        "short_message": "log_ingestion",
        "host":"nifi", 
        "facility":"CustomNifiGELF", 
        }''')
    flowFile.getAttributes().each { key,value ->
        try {
            myGelf << ["${key}": "${value}"]
        } catch(e) {
            log.warn('CustomNifiGELF impossible to append a KV from flowfile attributes to gelf {}', e)
        }
    }
    def myGelfText = JsonOutput.toJson(myGelf)
    log.debug('CustomNifiGELF msg {}', [myGelfText] as Object[])
    // overwrite flowfile content
    flowFile = session.write(flowFile, {outputStream ->
        outputStream.write(myGelfText.getBytes(StandardCharsets.UTF_8))
    } as OutputStreamCallback)
    session.transfer(flowFile, REL_SUCCESS)
} catch(e) {
    log.error('CustomNifiGELF Something went wrong {}', e)
    session.transfer(flowFile, REL_FAILURE)
}
