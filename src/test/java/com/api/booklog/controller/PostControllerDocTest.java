package com.api.booklog.controller;


import com.api.booklog.config.CustomWithMockUser;
import com.api.booklog.domain.Post;
import com.api.booklog.repository.post.PostRepository;
import com.api.booklog.repository.UserRepository;
import com.api.booklog.request.post.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.futsal.com", uriPort = 443)
public class PostControllerDocTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
//    private WebApplicationContext context;
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void clean() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("글 단건 조회")
    void Doc_findTest() throws Exception {
        // given
        Post post = Post.builder()
                .title("제목 1")
                .content("데이터 1")
                .build();
        postRepository.save(post);

        // exptected
        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/posts/{postId}", post.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                // 요청 파라미터
                .andDo(document("post-inquiry", pathParameters(
                        RequestDocumentation.parameterWithName("postId").description("게시글 Id")
                        ),
                        // 응답 json의 필드 설명
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("게시글 Id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")

                        )
                ));
    }

    @Test
    @CustomWithMockUser
    @DisplayName("글 등록")
    void Doc_writeTest() throws Exception {
        // given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // exptected
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/posts")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                // 요청 파라미터
                .andDo(document("post-create",
                                requestFields(
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목")
                                                // 제약사항 or 반드시 넘여겨야 할 enum값을 표기할 때
                                                .attributes(Attributes.key("constraint").value("욕설 없는 제목 입력해주세요")),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용").optional()  // 옵션, 즉 써도 되고 안써도 됨
                                )
                ));
    }

}

