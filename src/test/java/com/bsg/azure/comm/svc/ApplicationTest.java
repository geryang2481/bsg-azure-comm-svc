package com.bsg.azure.comm.svc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.bsg.azure.comm.svc.Application;

class ApplicationTest {
	
@InjectMocks
public Application application;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    void customOpenAPI(){
//        application.customOpenAPI();
//    }



    @Test
    void corsConfigurationSource(){
        //application.corsConfigurationSource();
    }
}
