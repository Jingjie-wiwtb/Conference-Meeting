package fudan.se.lab2.tool;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import fudan.se.lab2.domain.Paper;
import fudan.se.lab2.repository.ConferenceRepository;
import fudan.se.lab2.service.PaperService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Transactional
//系统你执行测试方法时，调用者是SpringRunnerClass类
@RunWith(SpringRunner.class)
@SpringBootTest
class ToolTest {

    @Autowired
    private ConferenceRepository conferenceRepository;
    @Autowired
    private PaperService paperService;

    @Test
    void listToPage() {
        List<Integer> testList = new ArrayList<>();
        for(int i = 0; i < 10; i++)
            testList.add(i);
        Pageable pageable = PageRequest.of(0,6);
        Pageable pageable_1 = PageRequest.of(1,6);
        Pageable pageable_2 = PageRequest.of(4,6);

        Page sample_1 = new PageImpl(testList.subList(0,6),pageable,6);
        Page sample_2 = new PageImpl(testList.subList(0,3),pageable,3);
        Page sample_3 = new PageImpl(testList.subList(0,8),pageable,8);
        Page sample_4 = new PageImpl(testList.subList(0,3),pageable_1,3);
        Page sample_5 = new PageImpl(testList.subList(0,6),pageable_2,10);

        List<Map<String,Object>> test = conferenceRepository.findSubmittingByChairId((long)6);
        Page<Map<String,Object>> resultPage = Tool.listToPage(test,PageRequest.of(0,10));
        System.out.println("testlist"+test.size());
        JSONObject jsonObject = Tool.pageToJsonResponse(resultPage,"conference");
        System.out.println(jsonObject.getInteger("number"));
        //List.size == pageSize
        Page test_1 = Tool.listToPage(testList.subList(0,6),pageable);
        Assert.assertEquals(sample_1.getTotalElements(),test_1.getTotalElements()); //总元素数
        Assert.assertEquals(sample_1.getSize(),test_1.getSize());  //当前页面大小

        //List.size < pageSize
        Page test_2 = Tool.listToPage(testList.subList(0,3),pageable);
        Assert.assertEquals(sample_2.getSize(),test_2.getSize());  //当前页面大小
        Assert.assertEquals(sample_2.isLast(),test_2.isLast());     //是否为最后一页

        //List.size > pageSize
        Page test_3 = Tool.listToPage(testList.subList(0,8),pageable);
        Assert.assertEquals(sample_3.isFirst(),test_3.isFirst());   //是否为第一页

        //List.size < pageSize, 第二页
        Page test_4 = Tool.listToPage(testList.subList(0,3),pageable_1);
        Assert.assertEquals(sample_4.getSize(),test_4.getSize());  //当前页面大小

        //List.size > pageSize,第二页
        Page test_5 = Tool.listToPage(testList.subList(0,3),pageable_1);
        Assert.assertEquals(sample_5.getSize(),test_5.getSize());  //当前页面大小
    }

    @Test
    void pageToJsonResponse() {
        List<Integer> testList = new ArrayList<>();
        for(int i = 0; i < 10; i++)
            testList.add(i);
        //page为空
        Pageable pageable = PageRequest.of(0,6);
        Page page_1 = Tool.listToPage(new ArrayList<>(),pageable);
        JSONObject test_1 = Tool.pageToJsonResponse(page_1,"conference");
        Assert.assertEquals(0,(int)test_1.getInteger("number"));
        //page不为空
        Page page_2 = Tool.listToPage(testList,pageable);
        JSONObject test_2 = Tool.pageToJsonResponse(page_2,"conference");
        //Assert.assertEquals(10,(int)test_1.getInteger("record"));
        Assert.assertEquals(6,(int)test_2.getInteger("number"));
    }

    @Test
    void getResponseEntityByString() {
        ResponseEntity result_1 = Tool.getResponseEntity("success");
        Assert.assertEquals(200, result_1.getStatusCode().value());
        ResponseEntity result_2 = Tool.getResponseEntity("fail");
        Assert.assertEquals(400, result_2.getStatusCode().value());
    }

    @Test
    void getResponseEntityByJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("number", 1);
        Assert.assertEquals(200, Tool.getResponseEntity(jsonObject).getStatusCode().value());

        jsonObject.clear();
        jsonObject.put("number", 0);
        Assert.assertEquals(400, Tool.getResponseEntity(jsonObject).getStatusCode().value());

        jsonObject.clear();
        jsonObject.put("message", "fail");
        Assert.assertEquals(400, Tool.getResponseEntity(jsonObject).getStatusCode().value());

        jsonObject.clear();
        jsonObject.put("message", "success");
        Assert.assertEquals(200, Tool.getResponseEntity(jsonObject).getStatusCode().value());
    }

    @Test
    void TransferStringArrayToString() {
        String[] array = {"topic1", "topic2", "topic3"};
        Assert.assertEquals("[\"topic1\",\"topic2\",\"topic3\"]", Tool.TransferStringArrayToString(array));
    }

    @Test
    void TransferStringToStringArray() {
        String s = "[\"topic1\",\"topic2\",\"topic3\"]";
        String[] array = {"topic1", "topic2", "topic3"};
        Assert.assertArrayEquals(array, Tool.TransferStringToStringArray(s));
    }

    @Test
    void RandomThree() throws NoSuchAlgorithmException {
        Tool tool = new Tool();
        HashSet<String> origin_members = new HashSet<>();
        origin_members.add("A");
        origin_members.add("B");
        origin_members.add("C");
        Assert.assertTrue(getReviewersUsernameSet(tool.RandomThree(origin_members)).containsAll(origin_members));
        origin_members.add("D");
        HashSet<String> usernames = getReviewersUsernameSet(tool.RandomThree(origin_members));
        Assert.assertTrue(usernames.contains("A") || usernames.contains("B"));
        Assert.assertFalse(tool.RandomThree(origin_members).getJSONObject(0).getBoolean("view"));
    }

    @Test
    void RandomThreeBasedOnBurden() throws JsonProcessingException, NoSuchAlgorithmException {
        Tool tool = new Tool();
        HashSet<String> origin_members = new HashSet<>();
        origin_members.add("A");
        origin_members.add("B");
        origin_members.add("C");
        origin_members.add("D");
        List<Paper> papers = new ArrayList<>();
        papers.add(paperService.findPaperById((long)2));
        papers.add(paperService.findPaperById((long)3));
        papers.add(paperService.findPaperById((long)4));
        papers.add(paperService.findPaperById((long)5));
        tool.RandomThreeBasedOnBurden(papers, origin_members);
        Assert.assertEquals("waiting", paperService.findPaperById((long)2).getStatus());
        Assert.assertEquals("waiting", paperService.findPaperById((long)3).getStatus());
        Assert.assertEquals("waiting", paperService.findPaperById((long)4).getStatus());
        Assert.assertEquals("waiting", paperService.findPaperById((long)5).getStatus());
        int a = 0 , b = 0, c = 0 , d = 0;
        for (long i = 2; i < 6; i++){
            JSONArray reviewers = JSONArray.parseArray(paperService.findPaperById(i).getReviewers());
            for (int j = 0; j < 3; j++){
                JSONObject reviewer = reviewers.getJSONObject(j);
                switch (reviewer.getString("username")){
                    case "A" : {
                        a++;
                        break;
                    }
                    case "B" : {
                        b++;
                        break;
                    }
                    case "C" : {
                        c++;
                        break;
                    }
                    case "D" : {
                        d++;
                        break;
                    }
                }
            }
        }
        Assert.assertEquals(3, a);
        Assert.assertEquals(3, b);
        Assert.assertEquals(3, c);
        Assert.assertEquals(3, d);
    }

    @Test
    void SubmitFile(){
        MockMultipartFile file = new MockMultipartFile("file", "filename.pdf", "text/plain", "content in file".getBytes());
        String result = Tool.SubmitFile(file);
//        Assert.assertTrue(result.startsWith("success"));
        Assert.assertFalse(result.substring(8).isEmpty());
    }

    @Test
    void PackageReviewers() throws NoSuchAlgorithmException {
        Tool tool = new Tool();
        HashSet<String> reviewers = new HashSet<>();
        reviewers.add("A");
        reviewers.add("B");
        reviewers.add("C");
        JSONArray result = tool.PackageReviewers(reviewers);
        JSONObject reviewer_1 = new JSONObject();
        reviewer_1.put("username", "A");
        reviewer_1.put("view", false);
        Assert.assertTrue(result.contains(reviewer_1));
        JSONObject reviewer_2 = new JSONObject();
        reviewer_2.put("username", "B");
        reviewer_2.put("view", false);
        Assert.assertTrue(result.contains(reviewer_2));
        JSONObject reviewer_3 = new JSONObject();
        reviewer_3.put("username", "B");
        reviewer_3.put("view", false);
        Assert.assertTrue(result.contains(reviewer_3));
    }

    private HashSet<String> getReviewersUsernameSet(JSONArray reviewersArray) {
        HashSet<String> result = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            JSONObject reviewer = reviewersArray.getJSONObject(i);
            result.add(reviewer.getString("username"));
        }
        return result;
    }

}