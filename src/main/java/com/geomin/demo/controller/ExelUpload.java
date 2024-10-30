package com.geomin.demo.controller;

import com.geomin.demo.dto.MedicalMaterialDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ExelUpload {

//    public static String fileName = "★2024.9.1.적용_(인체조직포함)_파일(급여).xlsx";
    public static String fileName = "★2024.9.1.적용_(인체조직포함)_파일(정액수가).xlsx";
    public static String filePath = "C:\\Users\\py\\Downloads";

    public static void main(String[] args) throws SQLException {

        PreparedStatement pstmt = null;
        Connection con = null;

        // 시작 시간 기록
        long startTime = System.currentTimeMillis();

        try{
            FileInputStream file = new FileInputStream(new File(filePath , fileName));

            Workbook workbook = new XSSFWorkbook(file);
            List<MedicalMaterialDTO> dataList = new ArrayList<>();

            // workbook의 첫번째 시트를 가져온다.
            Sheet workSheet = workbook.getSheetAt(0);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            AtomicInteger id = new AtomicInteger(26056);

            // Row, Cell 은 Sheet 안에 있는 행과 열을 의미
            // 첫번째 행 부터 끝까지 읽어라.
            // workSheet.getPhysicalNumberOfRows()
            for(int i = 1; i < workSheet.getPhysicalNumberOfRows(); i++){

                Row row = workSheet.getRow(i);

                MedicalMaterialDTO data = new MedicalMaterialDTO();

                data.setMmId(id.getAndIncrement());

                // row 첫번째에 있는 걸 문자열로 읽어서 가져와서 넣어라.
                data.setMmCode(row.getCell(0).getStringCellValue());

                Cell fdCell = row.getCell(1);

                if(fdCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(fdCell)){
                    data.setFirstDate(dateFormat.format(row.getCell(1).getDateCellValue()));
                }
                else if(fdCell.getCellType() == CellType.STRING){
                    data.setFirstDate(row.getCell(1).getStringCellValue());
                }

                Cell sdCell = row.getCell(2);

                if(sdCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(sdCell)){
                    data.setFirstDate(dateFormat.format(row.getCell(2).getDateCellValue()));
                }
                else if(sdCell.getCellType() == CellType.STRING){
                    data.setFirstDate(row.getCell(2).getStringCellValue());
                }

                data.setMidDivNm(row.getCell(4).getStringCellValue());

                Cell mdcCell = row.getCell(5);
                if(mdcCell.getCellType() == CellType.STRING){
                    data.setMidDivCode(row.getCell(5).getStringCellValue());
                }else if(mdcCell.getCellType() == CellType.NUMERIC){
                    data.setMidDivCode(String.valueOf(row.getCell(5).getNumericCellValue()));
                }

                data.setMmName(row.getCell(6).getStringCellValue());


                Cell mmsCell = row.getCell(7);
                if(mmsCell.getCellType() == CellType.STRING){
                    data.setMmStandard(row.getCell(7).getStringCellValue());
                }
                else if(mmsCell.getCellType() == CellType.NUMERIC){
                    data.setMmStandard(String.valueOf(row.getCell(7).getNumericCellValue()));
                }

                data.setMmEa(row.getCell(8).getStringCellValue());

                Cell mmpCell = row.getCell(9);
                if(mmpCell.getCellType() == CellType.NUMERIC){
                    data.setMmMaxPrc((int) row.getCell(9).getNumericCellValue());
                }else if(mmpCell.getCellType() == CellType.STRING){
                    data.setMmMaxPrc(0);
                }

                data.setManufacturer(row.getCell(10).getStringCellValue());
                data.setMmType(row.getCell(11).getStringCellValue());
                data.setDistributor(row.getCell(12).getStringCellValue());

                dataList.add(data);
            }


//            log.info("dataList::{}",dataList);


            Class.forName("org.mariadb.jdbc.Driver");

            con = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/geomin", "root" , "mariadb");

            String sql
                    = "Insert into medical_material(mm_id , mm_code, first_date, start_date, mid_div_nm," +
                    "mid_div_code, mm_name, mm_standard, mm_ea, mm_max_prc, manufacturer, mm_type, distributor)" +
                    "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = con.prepareStatement(sql);


            int cnt = 0;

            for(MedicalMaterialDTO data : dataList){

                ++cnt;

                pstmt.setInt(1, data.getMmId());
                pstmt.setString(2, data.getMmCode());
                pstmt.setString(3, data.getFirstDate());
                pstmt.setString(4, data.getStartDate());
                pstmt.setString(5, data.getMidDivNm());
                pstmt.setString(6, data.getMidDivCode());
                pstmt.setString(7, data.getMmName());
                pstmt.setString(8, data.getMmStandard());
                pstmt.setString(9, data.getMmEa());
                pstmt.setInt(10, data.getMmMaxPrc());
                pstmt.setString(11, data.getManufacturer());
                pstmt.setString(12, data.getMmType());
                pstmt.setString(13, data.getDistributor());

                pstmt.addBatch();
                pstmt.clearParameters();

                if((cnt%50) == 0){
                    pstmt.executeBatch();
                    pstmt.clearBatch();
                    con.commit();
                }
            }

            pstmt.executeBatch();
            con.commit();
        }
        catch (Exception e){
            e.printStackTrace();
            con.rollback();
            throw new RuntimeException();
        }
        finally {
            if(pstmt != null){
                pstmt.close();
            }
            if(con != null){
                con.close();
            }

            // 종료 시간 기록
            long endTime = System.currentTimeMillis();
            long duration = (endTime - startTime) / 1000; // 초 단위로 변환

            System.out.println(fileName + " 파일의 데이터, DB에 일괄 업데이트 완료.");
            System.out.println("총 소요 시간: " + duration + "초");
        }

    }

//    @PostMapping("/exel")
//    public String readExel(MultipartFile file) throws IOException {
//
//        List<MedicalMaterialDTO> dataList = new ArrayList<>();
//
//        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
//
//        // 확장자가 맞는지 확인하고 아니면 예외처리.
//        if(!extension.equals("xlsx") && !extension.equals("xls")){
//            log.info("엑셀 파일이 아닙니다. equals에서 걸림");
//            throw new IOException("엑셀파일이 아닙니다.");
//        }
//
//        InputStream is = file.getInputStream();
//        Tika tika = new Tika();
//        String mineType = tika.detect(is);
//
//        if(!mineType.equals("application/x-tika-ooxml")){
//            log.info("엑셀 파일이 아닙니다. tika에 걸림.");
//            return null;
//        }
//
//        // 엑셀파일 객체 null 초기화 대기.
//        Workbook workbook = null;
//
//        // HSSF - Excel 97(-2007) 파일 포맷을 사용할 때 사용 , ex) HSSFWorkbook, HSSFSheet
//        // XSSF - Excel 2007 OOXML (.xlsx) 파일 포맷을 사용할 때 사용 , ex) XSSFWorkbook, XSSFSheet
//        if(extension.equals("xlsx")){
//            workbook = new XSSFWorkbook(file.getInputStream());
//        }
//        else if(extension.equals("xls")){
//            workbook = new HSSFWorkbook(file.getInputStream());
//        }
//
//        // workbook의 첫번째 시트를 가져온다.
//        Sheet workSheet = workbook.getSheetAt(0);
//
//        // Row, Cell 은 Sheet 안에 있는 행과 열을 의미
//        // 첫번째 행 부터 끝까지 읽어라.
//        for(int i = 1; i < 2; i++){
//
//            Row row = workSheet.getRow(i);
//
//            MedicalMaterialDTO data = new MedicalMaterialDTO();
//            // row 첫번째에 있는 걸 문자열로 읽어서 가져와서 넣어라.
//            data.setMmCoed(row.getCell(0).getStringCellValue());
//            data.setFirstDate(row.getCell(1).getStringCellValue());
//            data.setStartDate(row.getCell(2).getStringCellValue());
//            data.setMidDivNm(row.getCell(4).getStringCellValue());
//            data.setMidDivCode(row.getCell(5).getStringCellValue());
//            data.setMmName(row.getCell(6).getStringCellValue());
//            data.setMmStandard(row.getCell(7).getStringCellValue());
//            data.setMmEa(row.getCell(8).getStringCellValue());
//            data.setMmMaxPrc((int) row.getCell(9).getNumericCellValue());
//            data.setManufacturer(row.getCell(10).getStringCellValue());
//            data.setMmType(row.getCell(11).getStringCellValue());
//            data.setDistributor(row.getCell(12).getStringCellValue());
//
//            dataList.add(data);
//        }
//
//
//        log.info("dataList::{}",dataList);
//
//        return null;
//    }

}
