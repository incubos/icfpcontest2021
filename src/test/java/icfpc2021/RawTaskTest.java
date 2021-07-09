package icfpc2021;

import com.fasterxml.jackson.databind.ObjectMapper;
import icfpc2021.model.RawTask;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RawTaskTest {
    static String s = "{\"hole\":[[55,80],[65,95],[95,95],[35,5],[5,5],[35,50],[5,95],[35,95],[45,80]],\"figure\":{\"edges\":[[2,5],[5,4],[4,1],[1,0],[0,8],[8,3],[3,7],[7,11],[11,13],[13,12],[12,18],[18,19],[19,14],[14,15],[15,17],[17,16],[16,10],[10,6],[6,2],[8,12],[7,9],[9,3],[8,9],[9,12],[13,9],[9,11],[4,8],[12,14],[5,10],[10,15]],\"vertices\":[[20,30],[20,40],[30,95],[40,15],[40,35],[40,65],[40,95],[45,5],[45,25],[50,15],[50,70],[55,5],[55,25],[60,15],[60,35],[60,65],[60,95],[70,95],[80,30],[80,40]]},\"epsilon\":150000}";
    @Test
    public void testRead() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        RawTask rawTask = mapper.readValue(s, RawTask.class);
    }

    @Test
    public void testEpsilonIn001() throws IOException {
        var mapper = new ObjectMapper();
        var task = mapper.readValue(Files.readString(Path.of("problems", "001.json")), RawTask.class);
        Assert.assertEquals(150_000, task.epsilon);
    }
}
