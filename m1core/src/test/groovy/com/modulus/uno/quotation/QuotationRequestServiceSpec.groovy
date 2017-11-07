package com.modulus.uno.quotation

import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.test.mixin.Mock
import org.springframework.mock.web.MockHttpServletRequest
import com.modulus.uno.SaleOrderService
import com.modulus.uno.SaleOrder
import com.modulus.uno.BusinessEntity
import com.modulus.uno.Company
import com.modulus.uno.BusinessEntityType

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(QuotationRequestService)
@Mock([Company, SaleOrder, BusinessEntity, QuotationContract, QuotationRequest])
class QuotationRequestServiceSpec extends Specification {

    def items = []
    SaleOrderService saleOrderServise = Mock(SaleOrderService)

    def setup() {
      service.saleOrderServise = saleOrderServise
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }

    void "Should get Sale order when send one quotationRequest"(){
      given:"get quotationRequest"
          def company = new Company(rfc:"JIGE930831NZ1",
                                bussinessName:"Apple Computers",
                                webSite:"http://www.apple.com",
                                employeeNumbers:40,
                                grossAnnualBilling:4000).save(validate:false)
        def businessEntity = new BusinessEntity(rfc:'XXX010101XXX', website:'http://www.iecce.mx',type:BusinessEntityType.FISICA).save(validate:false)
        def quotationContract = new QuotationContract(
                                                      client:businessEntity,
                                                      commission:10,
                                                      initDate: new Date(),
                                                      company:company
                                                      ).save(validate:false)
      and: "get quotationContract"
        def quotationRequest = new QuotationRequest(
                                                    commission:12,
                                                    description:"Alguna,",
                                                    amount:2000,
                                                    status: QuotationRequestStatus.SEND,
                                                    satConcept: SatConcept.S1,
                                                    quotationContract: quotationContract

                                                    ).save(validate:false)
      when:
        //service.requestProcessed(QuotationRequest quotationRequest)
        saleOrderServise.createOrderForQuotationRequest()
        def quotation = service.requestProcessed(quotationRequest)

      then:
        1==1
    }
}