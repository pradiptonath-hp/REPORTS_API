package com.infosys.ms;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.model.SearchInputs;
import com.infosys.model.SearchResults;
import com.infosys.service.ICourseMgmtService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/reporting/api")
@OpenAPIDefinition(
        info = @Info(
                title = "Reporting API",
                version = "1.0",
                description = "Reporting API supporting File Download operations",
                license = @License(name = "Infosys", url = "http://infosys.com"),
                contact = @Contact(url = "http://gigantic-server.com", name = "Joy", email = "Joy@gigagantic-server.com")
        )
       )
public class CoursesReportOperationsController {

	@Autowired
	private ICourseMgmtService courseService;
	
	
	
	@Operation(summary = "Get Courses Information",
            responses = {
                    @ApiResponse(description = "courses Info",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Wrong url")})
	@GetMapping("/courses")
	public ResponseEntity<?> fetchCourseCategories(){
		
		try {
			//use service
			Set<String> coursesInfo = courseService.showAllCourseCategories();
			return new ResponseEntity<Set<String>>(coursesInfo,HttpStatus.OK);
			
		}
		catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
    @Operation(summary = "Get all training modes",
            description = "Fetches all the training modes available in the system.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved training modes",
                             content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = String.class))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
		@GetMapping("/training-modes")
		public ResponseEntity<?> fetchTrainingModes(){
			
			try {
				Set<String> trainingModesInfo = courseService.showAllTrainingModes();
				return new ResponseEntity<Set<String>>(trainingModesInfo,HttpStatus.OK);
				
			}
			catch(Exception e) {
				return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}
		
    @Operation(summary = "Get all faculties",
            description = "Fetches all the faculties available in the system.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved faculties",
                             content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = String.class))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
		@GetMapping("/faculties")
		public ResponseEntity<?> fetchCourseCategories1(){
			
			try {
				Set<String> facultiesInfo = courseService.showAllFaculties();
				return new ResponseEntity<Set<String>>(facultiesInfo,HttpStatus.OK);
				
			}
			catch(Exception e) {
				return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
    @Operation(summary = "Search for courses",
            description = "Searches for courses based on the provided filter criteria.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Search filters",
                 content = @Content(schema = @Schema(implementation = SearchInputs.class))),
            responses = {
                @ApiResponse(responseCode = "200", description = "Successfully retrieved courses",
                             content = @Content(mediaType = "application/json", 
                             schema = @Schema(implementation = SearchResults.class))),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            })
		@PostMapping("/search")
		public ResponseEntity<?> fetchCoursesByFilters(@RequestBody SearchInputs inputs){
			
			try {
			   List<SearchResults> list = courseService.showCoursesByFilters(inputs);
			   return new ResponseEntity<List<SearchResults>>(list,HttpStatus.OK);
			}
			catch(Exception ex) {
			   return new ResponseEntity<String>(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		 }
		}
		@PostMapping("/pdf-report")
		public void showPdfReport(@RequestBody SearchInputs inputs, HttpServletResponse res) {
			
			try {
				   //set the response content type
				   res.setContentType("application/pdf");
				   //set  the content disposition header to response content going to browser as downloadable file
				   res.setHeader("Content-Disposition","attachment;fileName=courses.pdf");
				   //use service
				    courseService.generatePdfReport(inputs,res);
				  
				}
				catch(Exception e) {
					e.printStackTrace();
				}
		}	
			@PostMapping("/excel-report")
			public void showExcelReport(@RequestBody SearchInputs inputs, HttpServletResponse res) {
				
				try {
					   //set the response content type
					   res.setContentType("application/vnd.ms-excel");
					   //set  the content disposition header to response content going to browser as downloadable file
					   res.setHeader("Content-Disposition","attachment;fileName=courses.xls");
					   //use service
					    courseService.generateExcelReport(inputs,res);  
					}
					catch(Exception e) {
						e.printStackTrace();
					}
		
              }
			@GetMapping("/all-pdf-report")
			public void showPdfReportAllData(HttpServletResponse res) {
				
				try {
					   //set the response content type
					   res.setContentType("application/pdf");
					   //set  the content disposition header to response content going to browser as downloadable file
					   res.setHeader("Content-Disposition","attachment;fileName=courses.pdf");
					   //use service
					    courseService.generatePdfReportAllData(res);
					  
					}
					catch(Exception e) {
						e.printStackTrace();
					}
			}
			
			@GetMapping("/all-excel-report")
			public void showExcelReportAllData(HttpServletResponse res) {
				
				try {
					   
					   //set the response content type
					   res.setContentType("application/vnd.ms-excel");
					   //set  the content disposition header to response content going to browser as downloadable file
					   res.setHeader("Content-Disposition","attachment;fileName=courses.xls");
					   //use service
					    courseService.generateExcelReportAllData(res);  
					}
					catch(Exception e) {
						e.printStackTrace();
					}
		
              }
}