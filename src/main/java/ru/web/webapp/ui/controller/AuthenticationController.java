package ru.web.webapp.ui.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.web.webapp.ui.model.request.LoginRequestModel;

@RestController
public class AuthenticationController {

    @ApiOperation("User login")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Response Headers",
                    responseHeaders = {
                            @ResponseHeader(name = "authorization",
                                    description = "Bearer JWT value",
                                    response = String.class),
                            @ResponseHeader(name = "userId",
                                    description = "Public user ID value",
                                    response = String.class)
                    })
    })

    @PostMapping("/login")
    public void fakeLogin(@RequestBody LoginRequestModel loginRequestModel) {
        throw new IllegalStateException("This method should not be called. This method is implemented by Spring Security");
    }
}
