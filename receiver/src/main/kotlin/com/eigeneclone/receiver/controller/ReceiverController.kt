package com.eigeneclone.receiver.controller

import com.eigeneclone.receiver.service.ReceiverService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/rest/logs")
class ReceiverController constructor(
    private val receiverService: ReceiverService
) {

    @PostMapping
    fun logByPost(@RequestBody log: String, request: HttpServletRequest): ResponseEntity<String> {

        receiverService.log(log, getIpBy(request))

        return ResponseEntity.ok("OK")
    }

    fun getIpBy(request: HttpServletRequest): String {
        return request.getHeader("X-FORWARDED-FOR") ?: request.remoteAddr
    }
}