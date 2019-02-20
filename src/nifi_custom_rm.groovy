// Delete of a specified file at each incoming FlowFile
// Flowfile attribute **deletepath** gives the path to be deleted
// In case of success this Attribute is removed after deletion.
// Fails when file does not exists
flowFile = session.get()
if (!flowFile) return
try {
    log.info('CustomNifiRm start')
    rmrdir = flowFile.getAttribute('deletepath')
    def theFile = new File(rmrdir)
    if (!theFile.exists()) throw new IOException('deletepath file not found')
    def status = theFile.delete()
    if (!status) throw new RuntimeException('error during delete')
    flowFile.removeAttribute('deletepath')
    session.transfer(flowFile, REL_SUCCESS)
} catch(e) {
    flowFile = session.putAttribute(flowFile, 'message', 'CustomNifiRm Something went wrong')
    flowFile = session.putAttribute(flowFile, 'level', 'ERROR')
    log.error('CustomNifiRm Something went wrong {}', e)
    session.transfer(flowFile, REL_FAILURE)
}
