package thoxvi.imnote2.BaseDatas.Note;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

/**
 * Created by Thoxvi on 2017/3/1.
 */
public class NoteTest {
    private Note n;

    @Before
    public void beforeCreate() {
        n = new Note(123456, "testTitle", "testContent",Note.NOTE_STATUS_LIVE,123456);
    }

    @Test
    public void getID() throws Exception {
        Assert.assertEquals(n.getID(),123456);
    }

    @Test
    public void getTitle() throws Exception {
        Assert.assertEquals(n.getTitle(),"testTitle");
    }

    @Test
    public void getContent() throws Exception {
        Assert.assertEquals(n.getContent(),"testContent");
    }

    @Test
    public void getStatus() throws Exception {
        Assert.assertEquals(n.getStatus(),Note.NOTE_STATUS_LIVE);
    }

    @Test
    public void setTitle() throws Exception {
        n.setTitle("Titletest");
        Assert.assertEquals(n.getTitle(),"Titletest");
    }

    @Test
    public void getTime() throws Exception{
        Assert.assertEquals(n.getTime(),123456);
    }
    @Test
    public void setContent() throws Exception {
        n.setContent("Contenttest");
        Assert.assertEquals(n.getContent(),"Contenttest");
    }

    @Test
    public void setTime() throws Exception {
        long time=(new Date()).getTime();
        n.setTime(time);
        Assert.assertEquals(n.getTime(),time);
    }

    @Test
    public void setStatus() throws Exception {
        n.setStatus(Note.NOTE_STATUS_DEAD);
        Assert.assertEquals(n.getStatus(),Note.NOTE_STATUS_DEAD);
    }

    @Test
    public void killNote() throws Exception {
        n.killNote();
        Assert.assertEquals(n.getStatus(),Note.NOTE_STATUS_DEAD);
    }

    @Test
    public void reliveNote() throws Exception {
        n.reliveNote();
        Assert.assertEquals(n.getStatus(),Note.NOTE_STATUS_LIVE);

    }

}