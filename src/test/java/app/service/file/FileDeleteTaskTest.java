package app.service.file;

import app.database.entity.Ship;
import app.database.repository.ShipRepository;
import app.web.dto.ShipDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.List;

class FileDeleteTaskTest extends FileStorageServiceTest {

    @Autowired
    ShipRepository shipRepository;

    private FileDeleteTask fileDeleteTask;

    @BeforeEach
    public void setUp() {
        super.setUp();
        fileDeleteTask = new FileDeleteTask(userInfoRepository, shipPhotoRepository);
    }

    @Test
    void deletion() throws Exception {
        var userPh1 = fileStorageService.saveImage(new byte[]{1, 3, 4}, "userPh1.jpg", ImageKind.USER);
        var userPhOld = fileStorageService.saveImage(new byte[]{1, 3, 4}, "userPh1.jpg", ImageKind.USER);

        userInfo.setPhotoName(userPh1.getName());
        userInfoRepository.save(userInfo);

        var shipPh1 = fileStorageService.saveImage(new byte[]{1, 3, 4}, "ship1.jpg", ImageKind.SHIP);
        var shipPh2 = fileStorageService.saveImage(new byte[]{1, 3, 4}, "ship2.jpg", ImageKind.SHIP);
        var shipPh3 = fileStorageService.saveImage(new byte[]{1, 3, 4}, "ship3.jpg", ImageKind.SHIP);
        var shipPh4 = fileStorageService.saveImage(new byte[]{1, 3, 4}, "ship4.jpg", ImageKind.SHIP);

        setOldCreationTime(shipPh1, shipPh2, shipPh3, userPhOld);

        var shipDto = new ShipDto();
        shipDto.setName("ship1");
        shipDto.setDraft(12.0);
        shipDto.setLength(12.0);
        shipDto.setWidth(1.0);
        shipDto.setPhotoList(List.of(shipPh1.getName(), shipPh2.getName()));

        var ship = new Ship(userInfo, shipDto);
        shipRepository.save(ship);


        fileDeleteTask.run();

        Assertions.assertTrue(userPh1.exists());
        Assertions.assertFalse(userPhOld.exists());
        Assertions.assertTrue(shipPh1.exists());
        Assertions.assertTrue(shipPh2.exists());
        Assertions.assertFalse(shipPh3.exists());
        Assertions.assertTrue(shipPh4.exists());    // new file
    }

    private void setOldCreationTime(File... files) throws IOException {
        for (var file : files) {
            var attr = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
            FileTime time = FileTime.fromMillis(System.currentTimeMillis() - FileDeleteTask.SURVIVE_TIME * 2);
            attr.setTimes(time, time, time);
        }
    }
}