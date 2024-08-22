package com.infosys.service;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.infosys.entity.CourseDetails;
import com.infosys.model.SearchInputs;
import com.infosys.model.SearchResults;
import com.infosys.repository.ICourseDetailsRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Service("courseService")
public class CourseMgmtServiceImpl implements ICourseMgmtService{

	@Autowired
	private ICourseDetailsRepository courseRepo;
	
	@Override
	public Set<String> showAllCourseCategories() {
		// TODO Auto-generated method stub
		
		return courseRepo.getUniqueCourseCategories();
	}
	
	@Override
	public Set<String> showAllTrainingModes() {
		// TODO Auto-generated method stub
		
		return courseRepo.getUniqueTrainingModes();
	}

	@Override
	public Set<String> showAllFaculties() {
		// TODO Auto-generated method stub
		return courseRepo.getUniqueFacultyNames();
	}

	@Override
	public List<SearchResults> showCoursesByFilters(SearchInputs inputs) {
		
		
		//GET NOTNULL AND NON EMPTY STRING VALUES FROM THE INPUT OBJECT AND PREPARE ENTITY
		//OBJECT HAVING THAT NOT NULL DATA AND PLACE THAT ENTITY OBJECT INSIDE EXAMPLE OBJ
		CourseDetails entity  = new CourseDetails();
		
		
		//if(category!= null && !category.equals("")&&category.length()!=0) {
		//or we can use
		
		String category = inputs.getCourseCategory();
		if(StringUtils.hasLength(category))
		  entity.setCourseCategory(category);
		
		
		
		//if(facultyName!=null  && !facultyName.equals("")&& facultyName.length()!=0) {
		
		String facultyName = inputs.getFacultyName();
		if(StringUtils.hasLength(facultyName))	
		   entity.setFacultyName(facultyName);
		
		
		String trainingMode = inputs.getTrainingMode();
		if(StringUtils.hasLength(trainingMode)) 
			entity.setTrainingMode(trainingMode);
		
		
		LocalDateTime startDate = inputs.getStartsOn();
		if(!ObjectUtils.isEmpty(startDate))
			entity.setStartDate(startDate);
		
		Example<CourseDetails> example  = Example.of(entity);
		// for executing dynamic queries
		// perform the search operation with filter data
		
		List<CourseDetails> listEntities  = courseRepo.findAll(example);
		//convert List<Entity obj> to List<SearchResult
		
	     List<SearchResults> listResults = new ArrayList();
	     listEntities.forEach(course->{
	    	SearchResults result = new SearchResults();
	    	BeanUtils.copyProperties(course, result);
	    	listResults.add(result);
	     });
	 	return listResults;
	}
	
	@Override
	public void generatePdfReport(SearchInputs inputs, HttpServletResponse res) throws DocumentException, IOException {
		// get the SearchResult
		List<SearchResults> listResults = showCoursesByFilters(inputs);
		
		//create Document object(open PDF)
				Document document = new Document(PageSize.A4);
				//get pdf writer to write to the document and response object
				PdfWriter.getInstance(document,res.getOutputStream());
				document.open();
				//Define font for the paragraph
				Font font  = FontFactory.getFont(FontFactory.TIMES_BOLD);
				font.setSize(30);
				font.setColor(Color.RED);
				
				//create paragraph having content
				Paragraph para  = new Paragraph("Search Report of Courses",font);
				para.setAlignment(Paragraph.ALIGN_CENTER);
				
				//add paragraph to document
				document.add(para);
				
				//Display Search results as the pdf table
				PdfPTable table  = new PdfPTable(10);
				table.setWidthPercentage(70);
				table.setWidths(new float[] {3.0f,3.0f,3.0f,3.0f,3.0f,3.0f,3.0f,3.0f,3.0f,3.0f});
				table.setSpacingBefore(2.0f);
				
				
				//prepare heading row cells in the pdf table
				PdfPCell cell = new PdfPCell();
				cell.setBackgroundColor(Color.gray);
				cell.setPadding(5);
				Font cellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
				cellFont.setColor(Color.BLACK);
				
				
				cell.setPhrase(new Phrase("courseId",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("courseName",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("Category",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("facultyName",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("Location",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("Fee",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("Course Status",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("Training Mode",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("Admin Contact",cellFont));
				table.addCell(cell);
				cell.setPhrase(new Phrase("Start Date",cellFont));
				table.addCell(cell);
				
				//add data cells to pdf table
				listResults.forEach(result->{
					table.addCell(String.valueOf(result.getCourseId()));
					table.addCell(result.getCourseName());
					table.addCell(result.getCourseCategory());
					table.addCell(result.getFacultyName());
					table.addCell(result.getLocation());
					table.addCell(String.valueOf(result.getFee()));
					table.addCell(result.getCourseStatus());
					table.addCell(result.getTrainingMode());
					table.addCell(String.valueOf(result.getAdminContact()));
					table.addCell(result.getStartDate().toString());
					
				});
				//add table to document
				document.add(table);
				//close the document
				document.close();
	}
	
	@Override
	public void generateExcelReport(SearchInputs inputs, HttpServletResponse res) throws Exception {
		
		//get the SearchResult
		List<SearchResults> listResults = showCoursesByFilters(inputs);
		
		//create ExcelWorkBook
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		//create Sheet in the workbook
		HSSFSheet sheet1 = workbook.createSheet("CourseDetails");
		//create heading row in sheet1
		HSSFRow headerRow = sheet1.createRow(0);
		headerRow.createCell(0).setCellValue("CourseId");
		headerRow.createCell(1).setCellValue("CourseName");
		headerRow.createCell(2).setCellValue("Location");
		headerRow.createCell(3).setCellValue("CourseCategory");
		headerRow.createCell(4).setCellValue("FacultyName");
		headerRow.createCell(5).setCellValue("fee");
		headerRow.createCell(6).setCellValue("AdminContact");
		headerRow.createCell(7).setCellValue("TrainingMode");
		headerRow.createCell(8).setCellValue("StartDate");
		headerRow.createCell(9).setCellValue("courseStatus");
		int i=1;
		
		for(SearchResults result:listResults){    
			HSSFRow dataRow = sheet1.createRow(0);
			dataRow.createCell(0).setCellValue(result.getCourseId());
			dataRow.createCell(1).setCellValue(result.getCourseName());
			dataRow.createCell(2).setCellValue(result.getLocation());
			dataRow.createCell(3).setCellValue(result.getCourseCategory());
			dataRow.createCell(4).setCellValue(result.getFacultyName());
			dataRow.createCell(5).setCellValue(result.getFee());
			dataRow.createCell(6).setCellValue(result.getAdminContact());
			dataRow.createCell(7).setCellValue(result.getTrainingMode());
			dataRow.createCell(8).setCellValue(result.getStartDate());
			dataRow.createCell(9).setCellValue(result.getCourseStatus());
			i++;
		}
		
		//get output stream pointing to response object
		ServletOutputStream outputStream = res.getOutputStream();
		//write excel workbook data response object using the  above stream
		workbook.write(outputStream);
		//close the workbook
		outputStream.close();
		workbook.close();
	  }

	@Override
	public void generatePdfReportAllData(HttpServletResponse res) throws Exception {

		// get all the data in the db table
        List<CourseDetails> list = courseRepo.findAll();
        List<SearchResults> listResults = new ArrayList();
        //copy List<CourseDetails> to List<SearchResults>
        list.forEach(course->{
      	  SearchResults result = new SearchResults();
      	  BeanUtils.copyProperties(course, result);
      	  listResults.add(result);
        });

		//create Document object(open PDF)
		Document document = new Document(PageSize.A4);
		//get pdf writer to write to the document and response object
		PdfWriter.getInstance(document,res.getOutputStream());
		document.open();
		//Define font for the paragraph
		Font font  = FontFactory.getFont(FontFactory.TIMES_BOLD);
		font.setSize(30);
		font.setColor(Color.RED);
		
		//create paragraph having content
		Paragraph para  = new Paragraph("Search Report of Courses",font);
		para.setAlignment(Paragraph.ALIGN_CENTER);
		
		//add paragraph to document
		document.add(para);
		
		//Display Search results as the pdf table
		PdfPTable table  = new PdfPTable(10);
		table.setWidthPercentage(70);
		table.setWidths(new float[] {3.0f,3.0f,3.0f,3.0f,3.0f,3.0f,3.0f,3.0f,3.0f,3.0f});
		table.setSpacingBefore(2.0f);
		
		
		//prepare heading row cells in the pdf table
		PdfPCell cell = new PdfPCell();
		cell.setBackgroundColor(Color.gray);
		cell.setPadding(5);
		Font cellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
		cellFont.setColor(Color.BLACK);
		
		
		cell.setPhrase(new Phrase("courseId",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("courseName",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Category",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("facultyName",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Location",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Fee",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Course Status",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Training Mode",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Admin Contact",cellFont));
		table.addCell(cell);
		cell.setPhrase(new Phrase("Start Date",cellFont));
		table.addCell(cell);
		
		//add data cells to pdf table
		list.forEach(result->{
			table.addCell(String.valueOf(result.getCourseId()));
			table.addCell(result.getCourseName());
			table.addCell(result.getCourseCategory());
			table.addCell(result.getFacultyName());
			table.addCell(result.getLocation());
			table.addCell(String.valueOf(result.getFee()));
			table.addCell(result.getCourseStatus());
			table.addCell(result.getTrainingMode());
			table.addCell(String.valueOf(result.getAdminContact()));
			table.addCell(result.getStartDate().toString());
			
		});
		//add table to document
		document.add(table);
		//close the document
		document.close();
	
	}

	@Override
	public void generateExcelReportAllData(HttpServletResponse res) throws Exception {
		// get all the data in the db table
          List<CourseDetails> list = courseRepo.findAll();
          List<SearchResults> listResults = new ArrayList();
          //copy List<CourseDetails> to List<SearchResults>
          list.forEach(course->{
        	  SearchResults result = new SearchResults();
        	  BeanUtils.copyProperties(course, result);
        	  listResults.add(result);
          });
		
		//create ExcelWorkBook
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		//create Sheet in the workbook
		HSSFSheet sheet1 = workbook.createSheet("CourseDetails");
		//create heading row in sheet1
		HSSFRow headerRow = sheet1.createRow(0);
		headerRow.createCell(0).setCellValue("CourseId");
		headerRow.createCell(1).setCellValue("CourseName");
		headerRow.createCell(2).setCellValue("Location");
		headerRow.createCell(3).setCellValue("CourseCategory");
		headerRow.createCell(4).setCellValue("FacultyName");
		headerRow.createCell(5).setCellValue("fee");
		headerRow.createCell(6).setCellValue("AdminContact");
		headerRow.createCell(7).setCellValue("TrainingMode");
		headerRow.createCell(8).setCellValue("StartDate");
		headerRow.createCell(9).setCellValue("courseStatus");
		int i=1;
		
		for(SearchResults result:listResults){    
			HSSFRow dataRow = sheet1.createRow(0);
			dataRow.createCell(0).setCellValue(result.getCourseId());
			dataRow.createCell(1).setCellValue(result.getCourseName());
			dataRow.createCell(2).setCellValue(result.getLocation());
			dataRow.createCell(3).setCellValue(result.getCourseCategory());
			dataRow.createCell(4).setCellValue(result.getFacultyName());
			dataRow.createCell(5).setCellValue(result.getFee());
			dataRow.createCell(6).setCellValue(result.getAdminContact());
			dataRow.createCell(7).setCellValue(result.getTrainingMode());
			dataRow.createCell(8).setCellValue(result.getStartDate());
			dataRow.createCell(9).setCellValue(result.getCourseStatus());
			i++;
		}
		
		//get output stream pointing to response object
		ServletOutputStream outputStream = res.getOutputStream();
		//write excel workbook data response object using the  above stream
		workbook.write(outputStream);
		//close the workbook
		outputStream.close();
		workbook.close();
	}
   }
