package com.modulus.uno

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification
import java.lang.Void as Should
import spock.lang.FailsWith

@TestFor(TrackingService)
@Mock([PurchaseOrder,Bank,TrackingLogLink,LogRecord,State])
class TrackingServiceSpec extends Specification {

  Should "create the log register for instance"(){
    given:"the instance"
      PurchaseOrder purchaseOrder = new PurchaseOrder()
      purchaseOrder.save(validate:false)
    and:"its tracking log link"
      TrackingLogLink trackingLogLink = service.createTrackingLogForThisInstance(purchaseOrder) 
    and:"the current state of the instance in the machine"
      State state = new State()
      state.save(validate:false)
    when:
      LogRecord record = service.addRecordToInstanceLog(purchaseOrder,state.id)
    then:
      record.id
      record.currentState.id == state.id
  }

  Should "get the records of the log"(){
    given:"the instance and its tracking log"
      PurchaseOrder purchaseOrder = new PurchaseOrder()
      purchaseOrder.save(validate:false)
      createTrackingLog(purchaseOrder)
    when:
      ArrayList<LogRecord> logRecords = service.findTrackingLogOfInstance(purchaseOrder)
    then:
      logRecords.size() == 3
  }

  Should "get the last record of the instance transition"(){
    given:"the instance"
      PurchaseOrder purchaseOrder = new PurchaseOrder()
      purchaseOrder.save(validate:false)
      createTrackingLog(purchaseOrder)
    when:
      LogRecord logRecord = service.findLastTrackingLogRecord(purchaseOrder)
    then:
      logRecord.currentState.finalState
  }

  private createTrackingLog(PurchaseOrder purchaseOrder){
    service.createTrackingLogForThisInstance(purchaseOrder)
    ArrayList<State> states = [new State(),new State(),new State(finalState:true)]
    states.each{ state ->
      state.save(validate:false)
      service.addRecordToInstanceLog(purchaseOrder,state.id)
    }
  }

}
