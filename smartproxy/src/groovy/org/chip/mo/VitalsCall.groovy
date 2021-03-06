package org.chip.mo;

import org.chip.mo.exceptions.MOCallException;
import org.chip.rdf.Vitals;
import org.chip.rdf.vitals.*;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

import groovy.xml.MarkupBuilder;

class VitalsCall extends MilleniumObjectCall{
	
	private static final String ENCOUNTERIDSPARAM = "ENCOUNTERIDSPARAM"
	
	Map vitalSignsGroupByEncounter = new HashMap()
	
	private static final String EVENTCODEHEIGHT
	private static final String EVENTCODEWEIGHT
	private static final String EVENTCODERRATE
	private static final String EVENTCODEHEARTRATE
	private static final String EVENTCODEOSAT
	private static final String EVENTCODETEMP
	private static final String EVENTCODESYS
	private static final String EVENTCODEDIA
	private static final String EVENTCODELOCATION
	private static final String EVENTCODEPOSITION
	private static final String EVENTCODEBPMETHOD
	private static final String EVENTCODESYSSUPINE
	private static final String EVENTCODESYSSITTING
	private static final String EVENTCODESYSSTANDING
	private static final String EVENTCODEDIASUPINE
	private static final String EVENTCODEDIASITTING
	private static final String EVENTCODEDIASTANDING
	
	static final Map encounterResourceMap
	static final Map encounterTitleMap
	static final Map vitalTypeMap
	static final Map vitalTitleMap
	static final Map vitalResourceMap
	static final Map vitalUnitMap
	static final Set vitalEventCodesSet
	static final Set bpEventCodesSet
	static final Map bpEventCodeByComplexBPEventCode
	static final Map bodyPositionByComplexBPEventCode
	
	static{
		
		def config = ConfigurationHolder.config
		
		EVENTCODEHEIGHT=config.cerner.mo.eventCode.EVENTCODEHEIGHT
		EVENTCODEWEIGHT=config.cerner.mo.eventCode.EVENTCODEWEIGHT
		EVENTCODERRATE=config.cerner.mo.eventCode.EVENTCODERRATE
		EVENTCODEHEARTRATE=config.cerner.mo.eventCode.EVENTCODEHEARTRATE
		EVENTCODEOSAT=config.cerner.mo.eventCode.EVENTCODEOSAT
		EVENTCODETEMP=config.cerner.mo.eventCode.EVENTCODETEMP
		EVENTCODESYS=config.cerner.mo.eventCode.EVENTCODESYS
		EVENTCODEDIA=config.cerner.mo.eventCode.EVENTCODEDIA
		EVENTCODELOCATION=config.cerner.mo.eventCode.EVENTCODELOCATION
		EVENTCODEPOSITION=config.cerner.mo.eventCode.EVENTCODEPOSITION
		EVENTCODEBPMETHOD=config.cerner.mo.eventCode.EVENTCODEBPMETHOD
		EVENTCODESYSSUPINE=config.cerner.mo.eventCode.EVENTCODESYSSUPINE
		EVENTCODESYSSITTING=config.cerner.mo.eventCode.EVENTCODESYSSITTING
		EVENTCODESYSSTANDING=config.cerner.mo.eventCode.EVENTCODESYSSTANDING
		EVENTCODEDIASUPINE=config.cerner.mo.eventCode.EVENTCODEDIASUPINE
		EVENTCODEDIASITTING=config.cerner.mo.eventCode.EVENTCODEDIASITTING
		EVENTCODEDIASTANDING=config.cerner.mo.eventCode.EVENTCODEDIASTANDING
		
		encounterResourceMap = config.cerner.mo.encounterResource
		
		encounterTitleMap = config.cerner.mo.encounterTitle
		
		vitalTypeMap = readEventCodesConfigMap(config.cerner.mo.eventCode, config.cerner.mo.vitalsType)
		
		vitalTitleMap = readEventCodesConfigMap(config.cerner.mo.eventCode, config.cerner.mo.vitalsTitle)
		vitalTitleMap.putAll(readEventTagConfigMap(config.cerner.mo.vitalsTitleTagMap))
		
		vitalResourceMap = readEventCodesConfigMap(config.cerner.mo.eventCode, config.cerner.mo.vitalResource)
		vitalResourceMap.putAll(readEventTagConfigMap(config.cerner.mo.vitalResourceTagMap))
		
		vitalUnitMap = readEventCodesConfigMap(config.cerner.mo.eventCode, config.cerner.mo.vitalUnits)
		
		vitalEventCodesSet = new HashSet()
		vitalEventCodesSet.add(EVENTCODEHEIGHT)
		vitalEventCodesSet.add(EVENTCODEWEIGHT)
		vitalEventCodesSet.add(EVENTCODERRATE)
		vitalEventCodesSet.add(EVENTCODEHEARTRATE)
		vitalEventCodesSet.add(EVENTCODEOSAT)
		vitalEventCodesSet.add(EVENTCODETEMP)
		vitalEventCodesSet.add(EVENTCODESYS)
		vitalEventCodesSet.add(EVENTCODEDIA)
		vitalEventCodesSet.add(EVENTCODEBPMETHOD)
		vitalEventCodesSet.add(EVENTCODELOCATION)
		vitalEventCodesSet.add(EVENTCODEPOSITION)
		vitalEventCodesSet.add(EVENTCODESYSSUPINE)
		vitalEventCodesSet.add(EVENTCODESYSSITTING)
		vitalEventCodesSet.add(EVENTCODESYSSTANDING)
		vitalEventCodesSet.add(EVENTCODEDIASUPINE)
		vitalEventCodesSet.add(EVENTCODEDIASITTING)
		vitalEventCodesSet.add(EVENTCODEDIASTANDING)
		
		bpEventCodesSet = new HashSet()
		bpEventCodesSet.add(EVENTCODESYS)
		bpEventCodesSet.add(EVENTCODEDIA)
		bpEventCodesSet.add(EVENTCODELOCATION)
		bpEventCodesSet.add(EVENTCODEPOSITION)
		bpEventCodesSet.add(EVENTCODEBPMETHOD)
		
		bpEventCodeByComplexBPEventCode = new HashMap()
		bpEventCodeByComplexBPEventCode.put(EVENTCODESYSSUPINE, EVENTCODESYS)
		bpEventCodeByComplexBPEventCode.put(EVENTCODESYSSITTING, EVENTCODESYS)
		bpEventCodeByComplexBPEventCode.put(EVENTCODESYSSTANDING, EVENTCODESYS)
		bpEventCodeByComplexBPEventCode.put(EVENTCODEDIASUPINE, EVENTCODEDIA)
		bpEventCodeByComplexBPEventCode.put(EVENTCODEDIASITTING, EVENTCODEDIA)
		bpEventCodeByComplexBPEventCode.put(EVENTCODEDIASTANDING, EVENTCODEDIA)
		
		bodyPositionByComplexBPEventCode = new HashMap()
		bodyPositionByComplexBPEventCode.put(EVENTCODESYSSUPINE, 'Supine')
		bodyPositionByComplexBPEventCode.put(EVENTCODESYSSITTING, 'Supine')
		bodyPositionByComplexBPEventCode.put(EVENTCODESYSSTANDING, 'Sitting')
		bodyPositionByComplexBPEventCode.put(EVENTCODEDIASUPINE, 'Sitting')
		bodyPositionByComplexBPEventCode.put(EVENTCODEDIASITTING, 'Standing')
		bodyPositionByComplexBPEventCode.put(EVENTCODEDIASTANDING, 'Standing')
		
		
		
	}
	
	static def readEventCodesConfigMap(eventCodesMap, propertiesMap){
		def hashMap = new HashMap()
		propertiesMap.each{propertiesMapEntry->
			String key = eventCodesMap.get(propertiesMapEntry.getKey())
			hashMap.put(key,  propertiesMapEntry.getValue())
		}
		return hashMap
	}
	
	static def readEventTagConfigMap(propertiesMap){
		def hashMap = new HashMap()
		propertiesMap.each{propertiesMapEntry->
			String key = propertiesMapEntry.getKey()
			if (key.indexOf("_")>0){
				key = key.replaceAll("_", " ") 
			}
			hashMap.put(key, propertiesMapEntry.getValue())
		}
			return hashMap
	}
	
	def makeCall(recordId, moURL)throws MOCallException{
		
		transaction = 'ReadEncountersByFilters'
		targetServlet = 'com.cerner.encounter.EncounterServlet'
		
		Map<String,Object> requestParams = new HashMap()
		requestParams.put(RECORDIDPARAM, recordId)
		
		def requestXML = createRequest(requestParams)
		//long l1 = new Date().getTime()
		def resp = makeRestCall(requestXML, moURL)
		handleExceptions(resp, recordId)
		//long l2 = new Date().getTime()
		//println("encounter mo call took : "+(l2-l1)/1000)
		readResponse(resp)
		//println("no of encounters: "+vitalSignsGroupByEncounter.size())
		
		//refresh the writer and builder objects
		writer = new StringWriter()
		builder = new MarkupBuilder(writer)
		
		transaction = 'ReadResultsByCount'
		targetServlet = 'com.cerner.results.ResultsServlet'
		
		requestParams.put(ENCOUNTERIDSPARAM, vitalSignsGroupByEncounter.keySet())
		requestXML = createRequest(requestParams)
		//l1 = new Date().getTime()
		resp=makeRestCall(requestXML, moURL)
		handleExceptions(resp, recordId)
		//l2 = new Date().getTime()
		//println("vitals mo call took : "+(l2-l1)/1000)

		readResponse(resp)
	}
	
	/**
	* Generates MO requests to 
	* - Get Encounters for a given patient ID
	* - Get Vitals for a list of Encounters and a given patient id
	* @param recordId
	* @return
	*/
	def generatePayload(requestParams){
		def recordId = (String)requestParams.get(RECORDIDPARAM)
		if (transaction.equals("ReadEncountersByFilters")){
			builder.PersonId(recordId)
			builder.BypassOrganizationSecurityIndicator('true')
		}else{
			builder.PersonId(recordId)
			builder.EventCount('999')
			builder.EventSet(){
				Name('CLINICAL INFORMATION')
			}
			
			Set encounterIdSet = (Set)requestParams.get(ENCOUNTERIDSPARAM)
			builder.EncounterIds(){
				encounterIdSet.each{encounterId->
					EncounterId(encounterId)
				}
			}
		}
	}
	
	/**
	* Reading the MO response for Vitals:
	* Iterate through all NumericResults
	* 	• if the event code is in the vitalEventCodesSet and value is valid
	* 		∘ start creating the vitals object
	* 		∘ specify if the added vital is a bpevent
	* 		∘ Add the vital to a list. This list will be added to a map which has parent event id as the key. 
	* 			So all vitals with the same parent event id are grouped together.
	* 		∘ If can't match by parent event id, match by timestamps.
	* 
	* Iterate through all CodedResults	
	* 	• if the event code is in the vitalEventCodesSet and value is valid
	* 		∘ start creating the vitals object
	* 		∘ specify if the added vital is a bpevent
	* 		∘ specify that the vital is codedfield
	* 		∘ Add the vital to a list. This list will be added to a map which has parent event id as the key. So all vitals with the same parent event id are grouped together.
	* 		∘ If can't match by parent event id, match by timestamps.
	* @param moResponse
	* @return
	*/
	def readResponse(moResponse)throws MOCallException{
		try{
			def replyMessage = moResponse.getData()
			def payload= replyMessage.Payload
			if (transaction.equals("ReadEncountersByFilters")){
				//long l1 = new Date().getTime()
				// Filter out inpatient encounters, per 12/19/2011 decision.
				payload.Encounters.Encounter.findAll {
				  it.EncounterTypeClass.Display.text() != "Inpatient" 
				}.each {
					Encounter encounter = new Encounter()
					encounter.setId(it.EncounterId.text())
					encounter.setStartDate(it.RegistrationDateTime.text())
					encounter.setEndDate(it.DischargeDateTime.text())
					encounter.setResource(encounterResourceMap.get(it.EncounterTypeClass.Display.text()))
					encounter.setTitle(encounterTitleMap.get(it.EncounterTypeClass.Display.text()))
					VitalSignsGroup vitalSignsGroup = new VitalSignsGroup()
					vitalSignsGroup.setEncounter(encounter)
					vitalSignsGroupByEncounter.put(it.EncounterId.text(), vitalSignsGroup)
				}
				//long l2 = new Date().getTime()
				//println("encounter reading moresponse took: "+(l2-l1)/1000)
			}else{
				//int i = 0
				//long l1 = new Date().getTime()
				payload.Results.ClinicalEvents.NumericResult.each{ currentNumericResult->
						//i++
						def currentEncounterId = currentNumericResult.EncounterId.text()
						def currentEventCode = currentNumericResult.EventCode.Value.text()
						def currentValue = currentNumericResult.Value.text()
						def currentEventId = currentNumericResult.EventId.text()
						def currentParentEventId = currentNumericResult.ParentEventId.text()
						def currentEventEndDateTime = currentNumericResult.EventEndDateTime.text()
						def currentUpdateDateTime = currentNumericResult.UpdateDateTime.text()
						
						
						currentValue=convertValue(currentValue, currentEventCode)
						
						if((currentEventCode!=null) && (vitalEventCodesSet.contains(currentEventCode)) && valueIsValid(currentValue)){
							VitalSign vitalSign = new VitalSign()
							vitalSign.setEventId(currentEventId)
							vitalSign.setParentEventId(currentParentEventId)
							vitalSign.setValue(currentValue)
							vitalSign.setCode(currentEventCode)
							vitalSign.setType(vitalTypeMap.get(currentEventCode))
							vitalSign.setTitle(vitalTitleMap.get(currentEventCode))
							vitalSign.setResource(vitalResourceMap.get(currentEventCode))
							vitalSign.setUnit(vitalUnitMap.get(currentEventCode))
							vitalSign.setEventEndDateTime(currentEventEndDateTime)
							vitalSign.setUpdateDateTime(currentUpdateDateTime)
							(bpEventCodesSet.contains(currentEventCode))?vitalSign.setIsBPField(true):vitalSign.setIsBPField(false)
							
							addVitalSignToVitalSignsGroupByEncounter(vitalSign, currentEncounterId)
							
						}
				}	
				payload.Results.ClinicalEvents.CodedResult.each{ currentCodedResult->
						//i++
						def currentEncounterId = currentCodedResult.EncounterId.text()
						def currentEventCode = currentCodedResult.EventCode.Value.text()
						def currentEventTag = currentCodedResult.EventTag.text()
						def currentEventId = currentCodedResult.EventId.text()
						def currentParentEventId = currentCodedResult.ParentEventId.text()
						def currentEventEndDateTime = currentCodedResult.EventEndDateTime.text()
						def currentUpdateDateTime = currentCodedResult.UpdateDateTime.text()
						
						
						if((currentEventCode!=null) && (vitalEventCodesSet.contains(currentEventCode)) && valueIsValid(currentEventTag)){
							VitalSign vitalSign = new VitalSign()
							vitalSign.setEventId(currentEventId)
							vitalSign.setParentEventId(currentParentEventId)
							vitalSign.setValue(currentEventTag)
							vitalSign.setCode(currentEventCode)
							vitalSign.setType(vitalTypeMap.get(currentEventCode))
							vitalSign.setTitle(vitalTitleMap.get(currentEventTag))
							vitalSign.setResource(vitalResourceMap.get(currentEventTag))
							vitalSign.setEventEndDateTime(currentEventEndDateTime)
							vitalSign.setUpdateDateTime(currentUpdateDateTime)
							(bpEventCodesSet.contains(currentEventCode))?vitalSign.setIsBPField(true):vitalSign.setIsBPField(false)
							vitalSign.setIsCodedField(true)
							
							addVitalSignToVitalSignsGroupByEncounter(vitalSign, currentEncounterId)
						}
				}
				
				postProcessVitalSignsGroupByEncounter()
				//println("number of results returned : " + i)
				//long l2 = new Date().getTime()
				//println("vitals reading moresponse took: "+(l2-l1)/1000)
			}
		}catch(Exception e){
			throw new MOCallException("Error reading MO response", 500, e.getMessage())
		}
		return new Vitals(vitalSignsGroupByEncounter)
	}
	
	def addVitalSignToVitalSignsGroupByEncounter(VitalSign vitalSign, String currentEncounterId){
		if(vitalSignsGroupByEncounter.get(currentEncounterId).vitalSignsByParentEvent.keySet().contains(vitalSign.getParentEventId())){
			vitalSignsGroupByEncounter.get(currentEncounterId).vitalSignsByParentEvent.get(vitalSign.getParentEventId()).add(vitalSign)
		}else{
			//Unable to match on parentEventId
			//Try and match on timestamp first
			boolean matchFound = false
			//Get the set of all parentEventIds for this encounter
			Set parentEventIds = vitalSignsGroupByEncounter.get(currentEncounterId).vitalSignsByParentEvent.keySet()
			//Iterate through each parentEventId
			parentEventIds.each{ parentEventId ->
				//Get the vitalSignList attached to this parentEventId
				List vitalSignList = vitalSignsGroupByEncounter.get(currentEncounterId).vitalSignsByParentEvent.get(parentEventId)
				
				//compare the timestamps from the first element in the list with the current event's timestamps.
				String listEventEndDateTime = null
				String listUpdateDateTime = null
				if(vitalSignList.size()>0){
					listEventEndDateTime = ((VitalSign)vitalSignList.get(0)).getEventEndDateTime()
					listUpdateDateTime = ((VitalSign)vitalSignList.get(0)).getUpdateDateTime()
				}
				if(vitalSign.getEventEndDateTime()==listEventEndDateTime && vitalSign.getUpdateDateTime()==listUpdateDateTime){
					//timestamps match. Add the vital to this list.
					vitalSignsGroupByEncounter.get(currentEncounterId).vitalSignsByParentEvent.get(parentEventId).add(vitalSign)
					matchFound = true
				}
			}
			
			//Unable to match on timestamp. Create a new list for this parentEventId
			if(!matchFound){
				List<VitalSign> newVitalSignList = new ArrayList()
				newVitalSignList.add(vitalSign)
				vitalSignsGroupByEncounter.get(currentEncounterId).vitalSignsByParentEvent.put(vitalSign.getParentEventId(), newVitalSignList)
			}
			
		}
	}
	
	/**
	 * Overview:
	 * This method extracts atomic vital sign results from complex vital sign results.
	 * e.g. MO can return a vital sign result which contains a 'systolic bp' reading at 'supine' body position. (SYSTOLIC-98-SUPINE)
	 * This method will break this into two vital signs: 'systolic bp'(SYSTOLIC-98) reading and 'supine'(SUPINE) body position
	 * ****************************
	 * Data Structures explanation:
	 * 1. atomicVitalSignsMap holds the lists of newly created (atomic) vital sign objects, grouped by a parent event id and further by encounter id.
	 * 	-Each of these lists are mapped to a new parent event id.
	 * 	-This is done because:
	 * 		-e.g. SYSTOLIC-98-SUPINE and SYSTOLIC-100-SITTING complex results both have the same parent event id (as returned by MO)
	 * 		-After splitting these into atomic results:
	 * 			-SYSTOLIC-98 and SUPINE are given a unique parent event id.
	 * 			-SYSTOLIC-100 and SITTING are given a unique parent event id... and so on.
	 * 			-The two systolic readings are now correctly associated with a body position using the new parent event id.
	 * 
	 * 2. complexEncounterParentIdsMap holds the parent event ids(grouped by encounter id) of the vital sign lists that contain complex results. 
	 * ****************************
	 * Algorithm:
	 * -Iterate through all encounters
	 * 	-Add an entry in the atomicVitalSignsMap to store lists of new atomic vital sign objects for the current encounterId
	 * 		-Iterate through all parent event ids for the current encounter
	 * 			-Get the vitalSigns list for the current parent event id
	 * 			-IMPORTANT ASSUMPTION: if the first vital sign in the list is a complex bp result, assume all are and proceed to process them.
	 * 			-Split the list of complex results into seperate lists based on body position
	 * 			-Add the list to the atomicVitalSignsMap, grouped by parent event id and further by encounter id
	 * 			-Add the parent event id of the complex results to complexEncounterParentIdsMap, grouped by encounter id
	 * 	-Add the new lists from atomicVitalSignsMap to the vitalSignsGroupByEncounter
	 * 	-Remove the original lists of complex results (referenced by parent event ids in complexEncounterParentIdsMap) from the vitalSignsGroupByEncounter
	 * ****************************
	 */
	def postProcessVitalSignsGroupByEncounter(){
		Map atomicVitalSignsMap = new HashMap()
		Map complexEncounterParentIdsMap = new HashMap()
		
		Set encounterIds = vitalSignsGroupByEncounter.keySet()
		encounterIds.each{encounterId->
			
			atomicVitalSignsMap.put(encounterId, new VitalSignsGroup())
			complexEncounterParentIdsMap.put(encounterId, new ArrayList())
			
			Set parentEventIds = vitalSignsGroupByEncounter.get(encounterId).vitalSignsByParentEvent.keySet()
			parentEventIds.each{parentEventId->
				List vitalSignList = vitalSignsGroupByEncounter.get(encounterId).vitalSignsByParentEvent.get(parentEventId)
				if(vitalSignList.size()>0){
					if (bpEventCodeByComplexBPEventCode.keySet().contains(((VitalSign)vitalSignList.get(0)).getCode())){
						List supineBPVitalsList = new ArrayList()
						List standingBPVitalsList = new ArrayList()
						List sittingBPVitalsList = new ArrayList()
						
						Map bodyPositionListMap = new HashMap()
						bodyPositionListMap.put("Supine", supineBPVitalsList)
						bodyPositionListMap.put("Sitting", sittingBPVitalsList)
						bodyPositionListMap.put("Standing", standingBPVitalsList)
						
						vitalSignList.each{complexVitalSign->
							
							def bpEventCode = bpEventCodeByComplexBPEventCode.get(complexVitalSign.getCode())
							def bodyPosition = bodyPositionByComplexBPEventCode.get(complexVitalSign.getCode())
							
							if(bodyPositionListMap.get(bodyPosition).size()>0){//list has already been initialized. Only add the bp value
								bodyPositionListMap.get(bodyPosition).add(
									createVitalSignFromNumericResult(complexVitalSign.getEventId(),
									complexVitalSign.getParentEventId(),
									complexVitalSign.getValue(),
									bpEventCode,
									complexVitalSign.getEventEndDateTime(), complexVitalSign.getUpdateDateTime()))
							}else{// new list. Add both the bp value and body position
								bodyPositionListMap.get(bodyPosition).add(
									createVitalSignFromCodedResult(complexVitalSign.getEventId(),
										complexVitalSign.getParentEventId(),
										bodyPosition,
										EVENTCODEPOSITION,
										complexVitalSign.getEventEndDateTime(),
										complexVitalSign.getUpdateDateTime()))
								bodyPositionListMap.get(bodyPosition).add(
									createVitalSignFromNumericResult(complexVitalSign.getEventId(),
									complexVitalSign.getParentEventId(),
									complexVitalSign.getValue(),
									bpEventCode,
									complexVitalSign.getEventEndDateTime(), complexVitalSign.getUpdateDateTime()))
							}

							bodyPositionListMap.each{bodyPositionKey, bodyPositionList ->
								atomicVitalSignsMap.get(encounterId).vitalSignsByParentEvent.put(bodyPositionList.get(0).getEventId(), bodyPositionList)
							}
						}
						complexEncounterParentIdsMap.get(encounterId).add(parentEventId)
					}
				}
			}
		} 
			
		Set atomicVitalSignsMapKeySet = atomicVitalSignsMap.keySet()
		atomicVitalSignsMapKeySet.each { encounterId->
			Set parentEventIdSet = atomicVitalSignsMap.get(encounterId).vitalSignsByParentEvent.keySet()
			parentEventIdSet.each{ parentEventId->
				vitalSignsGroupByEncounter.get(encounterId).vitalSignsByParentEvent.put(parentEventId, atomicVitalSignsMap.get(encounterId).vitalSignsByParentEvent.get(parentEventId))
			}
		}
			
		Set complexEncounterParentIdsMapKeySet = complexEncounterParentIdsMap.keySet()
		complexEncounterParentIdsMapKeySet.each{encounterId->
			List complexParentIds = complexEncounterParentIdsMap.get(encounterId)
			complexParentIds.each{complexParentId->
				vitalSignsGroupByEncounter.get(encounterId).vitalSignsByParentEvent.remove(complexParentId)
			}
		}
	}
	
	def createVitalSignFromNumericResult(currentEventId, currentParentEventId, currentValue, currentEventCode, currentEventEndDateTime, currentUpdateDateTime){
		VitalSign vitalSign = new VitalSign()
		vitalSign.setEventId(currentEventId)
		vitalSign.setParentEventId(currentParentEventId)
		vitalSign.setValue(currentValue)
		vitalSign.setCode(currentEventCode)
		vitalSign.setType(vitalTypeMap.get(currentEventCode))
		vitalSign.setTitle(vitalTitleMap.get(currentEventCode))
		vitalSign.setResource(vitalResourceMap.get(currentEventCode))
		vitalSign.setUnit(vitalUnitMap.get(currentEventCode))
		vitalSign.setEventEndDateTime(currentEventEndDateTime)
		vitalSign.setUpdateDateTime(currentUpdateDateTime)
		(bpEventCodesSet.contains(currentEventCode))?vitalSign.setIsBPField(true):vitalSign.setIsBPField(false)
		return vitalSign
	}
	
	def createVitalSignFromCodedResult(currentEventId, currentParentEventId, currentEventTag, currentEventCode, currentEventEndDateTime, currentUpdateDateTime){
		VitalSign vitalSign = new VitalSign()
		vitalSign.setEventId(currentEventId)
		vitalSign.setParentEventId(currentParentEventId)
		vitalSign.setValue(currentEventTag)
		vitalSign.setCode(currentEventCode)
		vitalSign.setType(vitalTypeMap.get(currentEventCode))
		vitalSign.setTitle(vitalTitleMap.get(currentEventTag))
		vitalSign.setResource(vitalResourceMap.get(currentEventTag))
		vitalSign.setEventEndDateTime(currentEventEndDateTime)
		vitalSign.setUpdateDateTime(currentUpdateDateTime)
		(bpEventCodesSet.contains(currentEventCode))?vitalSign.setIsBPField(true):vitalSign.setIsBPField(false)
		vitalSign.setIsCodedField(true)
		return vitalSign
	}
	
	def convertValue(currentValue, currentEventCode){
		if (currentEventCode.equals(EVENTCODEHEIGHT)){
			double height = Double.parseDouble(currentValue)
			height = height/100
			currentValue = Double.toString(height)
		}
		return currentValue
	}
	
	def valueIsValid(currentValue){
		if (currentValue==null) return false
		if (currentValue.equals("")) return false
		if (currentValue.equals("0")) return false
		if (currentValue.equals("0.0")) return false
		
		return true
	}
}
