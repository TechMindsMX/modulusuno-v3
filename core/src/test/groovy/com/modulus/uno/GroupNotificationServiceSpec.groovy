package com.modulus.uno

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GroupNotificationService)
@Mock([GroupNotification, User])
class GroupNotificationServiceSpec extends Specification {

    def setup() {
    }

    def "Create a new notification group"() {
      given:"A users list"
        def user1= new User(username:"User1",enabled:true,
        profile:new Profile(name:"User1", email:"user1@me.com")).save(validate:false)
        def user2= new User(username:"User2",enabled:true,
        profile:new Profile(name:"User2", email:"user2@me.com")).save(validate:false)
        def user3= new User(username:"User3", enabled:true,
        profile:new Profile(name:"User3", email:"user3@me.com")).save(validate:false)
        ArrayList<User> userList = [user1, user2, user3]
      and:"a notification id"
        def notificationId = "586d4944e1d4ae54524dd622"
      when:"we want to save the group"
        def firstUserNotificationGroup = service.createGroup(notificationId, userList)
      then:"we should get"
        //firstUserNotificationGroup.notificationId == "586d4944e1d4ae54524dd622"
        //firstUserNotificationGroup.users == [user1, user2, user3]
        true == false
    }

    def "Update a notification group"() {
      given:"A user list for the first notification group"
        def user1= new User(username:"User1",enabled:true,
        profile:new Profile(name:"User1", email:"user1@me.com")).save(validate:false)
        def user2= new User(username:"User2",enabled:true,
        profile:new Profile(name:"User2", email:"user2@me.com")).save(validate:false)
        def user3= new User(username:"User3", enabled:true,
        profile:new Profile(name:"User3", email:"user3@me.com")).save(validate:false)
        ArrayList<User> userList = [user1, user2, user3]
      and:"A first notificationGroup"
        def firstNotificationGroup = new GroupNotification(emailerId:"586d4944e1d4ae54524dd622", users:userList).save()
      and:"A new users list for update"
        def user4= new User(username:"newUser1",enabled:true,
        profile:new Profile(name:"newUser1", email:"user1new@me.com")).save(validate:false)
        def user5= new User(username:"newUser2",enabled:true,
        profile:new Profile(name:"newUser2", email:"user2new@me.com")).save(validate:false)
        def user6= new User(username:"newUser3", enabled:true,
        profile:new Profile(name:"newUser3", email:"user3new@me.com")).save(validate:false)
        ArrayList<User> newUserList = [user4, user5, user6]

      and:"a notification id"
        def newNotificationId = "586d4944e1d4ae5diamon666"

      and:"a notification Group id"
        def groupId=firstNotificationGroup.id

      when:"we want to update the group"
        service.updateUsersGroup(groupId, newUsersList)
        service.updateNotifyId(groupId, notificationId)

      then:"we should get"
        //firstNotificationGroup.notificationId == newNotificationId
        //firstNotificationGroup.users == newUserList
        true == false
    }

}
