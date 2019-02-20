// Recursive delete of a specified directory at each incoming FlowFile
// Flowfile attribute **deletepath** gives the path to be deleted
// In case of success this Attribute is removed after deletion.
// Fails when directory does not exists
flowFile = session.get()
if (!flowFile) return
try {
    log.info('CustomNifiRmrdir start')
    rmrdir = flowFile.getAttribute('deletepath')
    def theDir = new File(rmrdir)
    if (!theDir.exists()) throw new IOException('deletepath directory not found')
    def status = theDir.deleteDir()
    if (!status) throw new RuntimeException('error during rmrdir delete')
    flowFile.removeAttribute('deletepath')
    session.transfer(flowFile, REL_SUCCESS)
} catch(e) {
    flowFile = session.putAttribute(flowFile, 'message', 'CustomNifiRmrdir Something went wrong')
    flowFile = session.putAttribute(flowFile, 'level', 'ERROR')
    log.error('CustomNifiRmrdir Something went wrong {}', e)
    session.transfer(flowFile, REL_FAILURE)
}
