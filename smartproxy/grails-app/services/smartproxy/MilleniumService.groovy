package smartproxy
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.chip.mo.exceptions.MOCallException

import groovy.xml.MarkupBuilder;
import groovyx.net.http.*
class MilleniumService {

    static transactional = false
	
	/**
	 * Maps the incoming transaction to a MilleniumObjectCall object.
	 */
	static Map requestMOMap = new HashMap()
	
	static {
		requestMOMap.put 'demographics', 'Demographics'
		requestMOMap.put 'problems', 'Problems'
		requestMOMap.put 'vital_signs', 'Vitals'
	}

	/**
     * Entry method into this service class.
     * Gets and transaction specific instance of the MilleniumObject Call object
     * Invokes makeCall on the MO Call object
     * @param payload
     * @param 
     * @return
     */
	def makeCall(transaction, recordId) throws MOCallException {
		if((recordId==null)||(recordId.trim().length()==0)){
			throw new MOCallException("Record ID not specified", 400, "")
		}
		def moCallObj = createMOCall(transaction)
		def moURL = ConfigurationHolder.config.grails.moURL
		moCallObj.makeCall(recordId, moURL)
    }
	
	/**
	 * Factory which instantiates the appropriate MilleniumObject using reflection
	 * @param transaction
	 * @return
	 */
	def createMOCall(transaction) throws MOCallException{
		def moCallObj
		try{
			def mappedTransaction = mapRequest(transaction)

			Class moCallClass = this.class.classLoader.loadClass('org.chip.mo.'+mappedTransaction+'Call')
			moCallObj=moCallClass.newInstance()
			moCallObj.init()
		}catch(Exception e){
			throw new MOCallException("Transaction \""+transaction+"\" not implemented", 501, e.getMessage())
		}
		return moCallObj
	}
	
	/**
	 * Takes in the incoming request parameter and converts it into a transaction
	 * @param string
	 * @return
	 */
	def mapRequest(transaction){
		return requestMOMap.get(transaction)
	}
	
}
