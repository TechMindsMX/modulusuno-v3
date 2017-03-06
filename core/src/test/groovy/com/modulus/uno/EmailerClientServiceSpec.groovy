package com.modulus.uno

import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.test.mixin.Mock

@TestFor(EmailerClientService)
@Mock([User])
class EmailerClientServiceSpec extends Specification {

  def setup() {
  }

  def "Create a list with id, subject of every emailer on emailer app"() {
    given:"A data of all emailers"
    def emailerStorage = [
    ["_id":"123abc", "dateCreated":2223232, "version":2, "lastUpdate":234343, "subject":"uno", "content":"<b>contentido</b>"],
    ["_id":"123abc", "dateCreated":2223232, "version":2, "lastUpdate":234343, "subject":"uno", "content":"<b>contentido</b>"],
    ["_id":"123abc", "dateCreated":2223232, "version":2, "lastUpdate":234343, "subject":"uno", "content":"<b>contentido</b>"]
    ]
    when:"We want to list the emailers by id and subject"
    def emailerList = service.obtainSubjectList(emailerStorage)
    then:"We should get a list with id and subject"
    emailerList == [["id":"123abc", "subject":"uno"], ["id":"123abc", "subject":"uno"], ["id":"123abc", "subject":"uno"]]
  }

  def "Get a subject of emailer"(){
    given:"An id of emailer"
    def idEmailer = "123abc"
    and: "A list of emailers"
    def emailerList = [["id":"123abc", "subject":"Subject Found"], ["id":"223abc", "subject":"uno"], ["id":"323abc", "subject":"dos"]]
    when:"We want to know the subject"
    def subjectEmailer = service.findSubject(idEmailer)
    then:"We should get"
    subjectEmailer == "Subject Found"
  }

  def "Get a list of emails"(){
    given:"A list of users"
    def user1= new User(username:"User1",enabled:true,
    profile:new Profile(email:"user1@me.com")).save(validate:false)
    def user2= new User(username:"User2",enabled:true,
    profile:new Profile(email:"user2@me.com")).save(validate:false)
    def users = [user1, user2]
    when:"We want to get the emails"
    def emails = service.getEmails(users)
    then:"We should get a list of emails"
    emails == ["user1@me.com","user2@me.com"]
  }

  def "Get a list of emailers with id and content"(){
    given:"A data of all emailers"
    def emailerStorage = [
    ["_id":"123abc", "dateCreated":2223232, "version":2, "lastUpdate":234343, "subject":"uno", "content":"<b>contenido</b>"],
    ["_id":"123abc", "dateCreated":2223232, "version":2, "lastUpdate":234343, "subject":"uno", "content":"<b>contenido</b>"],
    ["_id":"123abc", "dateCreated":2223232, "version":2, "lastUpdate":234343, "subject":"uno", "content":"<b>contenido</b>"]
    ]
    when:"We want to list the emailers by id and content"
    def emailerList = service.obtainContentList(emailerStorage)
    then:"We should get a list with id and subject"
    emailerList == [["id":"123abc", "content":"<b>contenido</b>"], ["id":"123abc", "content":"<b>contenido</b>"], ["id":"123abc", "content":"<b>contenido</b>"]]
  }

  def "Get the content of emailer"(){
    given:"An id of emailer"
    def idEmailer = "123abc"
    and: "A list of emailers"
    def emailerList = [["id":"123abc", "content":"<b>found!</b>"], ["id":"223abcd", "content":"<b>content</b>"], ["id":"323abc", "content":"<b>found!</b>"]]
    when:"We want to know the subject"
    def contentEmailer = service.findContent(idEmailer)
    then:"We should get"
    contentEmailer == "<b>found!</b>"
    contentEmailer != "This is not the content"
  }
}
