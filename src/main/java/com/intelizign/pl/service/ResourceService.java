package com.intelizign.pl.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.intelizign.pl.model.ResourceModel;
import com.intelizign.pl.repositories.ResourceRepository;

@Service
public class ResourceService {

	@Autowired
	private ResourceRepository resourceRepo;

	static String SHEET = "Sheet1";

	public ByteArrayInputStream ResourceExport() {
		List<ResourceModel> resourceModel = resourceRepo.findAllByOrderById();
		return ResourceInfoToExcel(resourceModel);
	}

	private ByteArrayInputStream ResourceInfoToExcel(List<ResourceModel> resourceModel) {

		String[] HEADERS = { "Emp Name", "Emp Code", "Username", "Email", "Emp Status", "Role Name", "Gender",
				"Designation", "Date Of Join", "Address", "Location", "Branch", "Country", "Department",
				"Mobile Number", "Report To", "CTC" };

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
			Sheet sheet = workbook.createSheet(SHEET);

			// Header
			Row headerRow = sheet.createRow(0);
			CellStyle cellStyle = workbook.createCellStyle();
			for (int col = 0; col < HEADERS.length; col++) {
				Cell cell = headerRow.createCell(col);
				cell.setCellValue(HEADERS[col]);
				addBold(workbook, cellStyle, cell);
			}

			int rowIdx = 1;
			for (ResourceModel resource : resourceModel) {

				Row row = sheet.createRow(rowIdx);
				row.createCell(0).setCellValue(resource.getEmp_name());
				row.createCell(1).setCellValue(resource.getEmpcode());
				row.createCell(2).setCellValue(resource.getUsername());
				row.createCell(3).setCellValue(resource.getEmail());
				row.createCell(4).setCellValue(resource.getEmp_status());
				row.createCell(5).setCellValue(resource.getResource_role());
				row.createCell(6).setCellValue(resource.getGender());
				row.createCell(7).setCellValue(resource.getDesignation());
				row.createCell(8).setCellValue(resource.getDate_of_join());
				row.createCell(9).setCellValue(resource.getAddress());
				row.createCell(10).setCellValue(resource.getLocation());
				row.createCell(11).setCellValue(resource.getBranch());
				row.createCell(12).setCellValue(resource.getCountry());
				row.createCell(13).setCellValue(resource.getDepartment());
				row.createCell(14).setCellValue(resource.getMobile_number());
				row.createCell(15).setCellValue(resource.getReport_to());
				row.createCell(16).setCellValue(resource.getCtc());
				rowIdx++;
			}
			workbook.write(out);

			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			return null;
		}

	}

	private void addBold(Workbook workbook, CellStyle cellStyle, Cell cell) {
		Font font = workbook.createFont();
		font.setBold(true);
		cellStyle.setFont(font);
		cell.setCellStyle(cellStyle);
	}

	public void save(MultipartFile file) {
		try {
			List<ResourceModel> resourceModels = excelToResources(file.getInputStream());
			resourceRepo.saveAll(resourceModels);
		} catch (IOException e) {
			throw new RuntimeException("fail to store excel data: " + e.getMessage());
		}
	}

	public static List<ResourceModel> excelToResources(InputStream input_stream) {
		try {
			Workbook workbook = new XSSFWorkbook(input_stream);

			Sheet sheet = workbook.getSheet(SHEET);
			Iterator<Row> rows = sheet.iterator();

			List<ResourceModel> resourceModels = new ArrayList<ResourceModel>();

			int rowNumber = 0;
			while (rows.hasNext()) {
				Row currentRow = rows.next();

				// skip header
				if (rowNumber == 0) {
					rowNumber++;
					continue;
				}

				Iterator<Cell> cellsInRow = currentRow.iterator();
				ResourceModel resourceModel = new ResourceModel();

				int cellIdx = 0;
				while (cellsInRow.hasNext()) {
					Cell currentCell = cellsInRow.next();

					switch (cellIdx) {
					case 0:
						resourceModel.setEmp_name((currentCell.getStringCellValue()));
						break;

					case 1:
						resourceModel.setEmpcode((currentCell.getStringCellValue()));
						break;

					case 2:
						resourceModel.setUsername((currentCell.getStringCellValue()));
						break;

					case 3:
						resourceModel.setEmail((currentCell.getStringCellValue()));
						break;

					case 4:
						resourceModel.setEmp_status((currentCell.getStringCellValue()));
						break;

					case 5:
						resourceModel.setResource_role((currentCell.getStringCellValue()));
						break;

					case 6:
						resourceModel.setGender((currentCell.getStringCellValue()));
						break;

					case 7:
						resourceModel.setDesignation((currentCell.getStringCellValue()));
						break;

					case 8:
						resourceModel.setDate_of_join((currentCell.getLocalDateTimeCellValue()));
						break;

					case 9:
						resourceModel.setAddress((currentCell.getStringCellValue()));
						break;

					case 10:
						resourceModel.setLocation((currentCell.getStringCellValue()));
						break;

					case 11:
						resourceModel.setBranch((currentCell.getStringCellValue()));
						break;

					case 12:
						resourceModel.setCountry((currentCell.getStringCellValue()));
						break;

					case 13:
						resourceModel.setDepartment((currentCell.getStringCellValue()));
						break;

					case 14:
						resourceModel.setMobile_number((currentCell.getStringCellValue()));
						break;

					case 15:
						resourceModel.setReport_to((currentCell.getStringCellValue()));
						break;

					case 16:
						resourceModel.setCtc((currentCell.getNumericCellValue()));
						break;

					default:
						break;
					}

					cellIdx++;
				}

				resourceModels.add(resourceModel);
			}
			workbook.close();
			return resourceModels;
		} catch (IOException e) {
			throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
		}
	}

	public List<String> getHeaderColumns(MultipartFile file, String sHEET) {
		List<String> RequestHeader = new ArrayList<>();

		Workbook workbook;
		Sheet sheet = null;
		try {

			workbook = new XSSFWorkbook(file.getInputStream());
			sheet = workbook.getSheet(sHEET);

		} catch (IOException e) {
			e.printStackTrace();
		}

		String testheader = sheet.getHeader().toString();
		System.out.println(testheader);
		Row header_row = sheet.getRow(0);
		Iterator<Cell> cellsInRow = header_row.iterator();
		while (cellsInRow.hasNext()) {
			Cell currentCell = cellsInRow.next();
			RequestHeader.add(currentCell.getStringCellValue());

		}
		return RequestHeader;
	}

}
