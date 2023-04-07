package sunset.java.etc.excel;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ExcelHandlerImpl implements ExcelHandler {
    private final String inputResource;
    private final String outputResource;

    @Override
    public void handle() throws Exception {
        XSSFWorkbook wb = new XSSFWorkbook(this.getClass().getClassLoader().getResourceAsStream(inputResource));
        List<String> languages = new ArrayList<>();
        for (Row row : wb.getSheetAt(0)) { // 첫 번째 시트
            if (row.getRowNum() < 1) // row: 1부터 시작, 1행은 스킵.
                continue;

            if (row.getCell(1) == null) // row 행, B열이 비어있으면 종료.
                break;

            languages.add(row.getCell(1).getStringCellValue()); // row 행, B열 추가
        }

        // file 없으면 에러남.
        URL output = this.getClass().getClassLoader().getResource(outputResource);
        FileOutputStream fo = new FileOutputStream(Paths.get(output.toURI()).toFile());

        String combinedLanguage = languages.stream().reduce((a, b) -> a.concat(", " + b)).orElse("");
        fo.write(combinedLanguage.getBytes());
    }
}
