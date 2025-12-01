package com.toy.userservice.account.adapter.`in`.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.toy.userservice.account.adapter.`in`.web.request.LoginRequest
import com.toy.userservice.account.adapter.`in`.web.request.RegisterUserRequest
import com.toy.userservice.account.application.port.`in`.LoginUseCase
import com.toy.userservice.account.application.port.`in`.RegisterUserUseCase
import com.toy.userservice.account.application.port.`in`.TokenPair
import com.toy.userservice.account.domain.model.*
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(UserController::class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    lateinit var registerUserUseCase: RegisterUserUseCase

    @MockkBean
    lateinit var loginUseCase: LoginUseCase

    @Test
    fun `회원가입 API 문서화`() {
        // given
        val request = RegisterUserRequest(
            email = "test@example.com",
            password = "Test1234!@#$",
            userRole = UserRole.BUYER
        )

        val mockUser = User.reconstitute(
            id = UserId.generate(),
            email = Email.of("test@example.com"),
            password = EncodedPassword.of("encoded-password-hash"),
            roles = setOf(UserRole.BUYER),
            status = UserStatus.ACTIVE,
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-01T00:00:00Z")
        )

        every { registerUserUseCase.execute(any()) } returns mockUser

        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andDo(
                document(
                    "user-register",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email")
                            .type(JsonFieldType.STRING)
                            .description("사용자 이메일 주소 (형식: example@domain.com)"),
                        fieldWithPath("password")
                            .type(JsonFieldType.STRING)
                            .description("비밀번호 (8-100자, 대문자/소문자/숫자/특수문자 각 1개 이상 포함)"),
                        fieldWithPath("userRole")
                            .type(JsonFieldType.STRING)
                            .description("사용자 역할 (BUYER: 구매자, SELLER: 판매자, ADMIN: 관리자)")
                    ),
                    responseFields(
                        fieldWithPath("success")
                            .type(JsonFieldType.BOOLEAN)
                            .description("API 처리 성공 여부"),
                        fieldWithPath("code")
                            .type(JsonFieldType.STRING)
                            .description("응답 코드"),
                        fieldWithPath("message")
                            .type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data")
                            .type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        fieldWithPath("data.id")
                            .type(JsonFieldType.STRING)
                            .description("생성된 사용자 ID (UUID)"),
                        fieldWithPath("data.email")
                            .type(JsonFieldType.STRING)
                            .description("사용자 이메일"),
                        fieldWithPath("data.roles")
                            .type(JsonFieldType.ARRAY)
                            .description("사용자 역할 목록"),
                        fieldWithPath("data.status")
                            .type(JsonFieldType.STRING)
                            .description("사용자 상태 (ACTIVE: 활성, INACTIVE: 비활성, SUSPENDED: 정지)"),
                        fieldWithPath("data.createdAt")
                            .type(JsonFieldType.STRING)
                            .description("생성 일시 (ISO-8601 형식)"),
                        fieldWithPath("timestamp")
                            .type(JsonFieldType.STRING)
                            .description("응답 생성 시각 (ISO-8601 형식)")
                    )
                )
            )
    }

    @Test
    fun `회원가입 실패 - 이메일 형식 오류`() {
        // given
        val invalidRequest = mapOf(
            "email" to "invalid-email-format",
            "password" to "Test1234!@#$",
            "userRole" to "BUYER"
        )

        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf())
        )
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "user-register-fail-invalid-email",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email")
                            .type(JsonFieldType.STRING)
                            .description("잘못된 형식의 이메일"),
                        fieldWithPath("password")
                            .type(JsonFieldType.STRING)
                            .description("비밀번호"),
                        fieldWithPath("userRole")
                            .type(JsonFieldType.STRING)
                            .description("사용자 역할")
                    )
                )
            )
    }

    @Test
    fun `회원가입 실패 - 비밀번호 조건 불충족`() {
        // given
        val invalidRequest = mapOf(
            "email" to "test@example.com",
            "password" to "weak",
            "userRole" to "BUYER"
        )

        // when & then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf())
        )
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "user-register-fail-weak-password",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email")
                            .type(JsonFieldType.STRING)
                            .description("이메일"),
                        fieldWithPath("password")
                            .type(JsonFieldType.STRING)
                            .description("약한 비밀번호 (조건 불충족)"),
                        fieldWithPath("userRole")
                            .type(JsonFieldType.STRING)
                            .description("사용자 역할")
                    )
                )
            )
    }

    @Test
    fun `로그인 API 문서화`() {
        // given
        val request = LoginRequest(
            email = "test@example.com",
            password = "Test1234!@#$"
        )

        val mockTokenPair = TokenPair(
            accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
            refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaWF0IjoxNTE2MjM5MDIyfQ.4Adcj0vGXH1C7RQWFX5V9ViJmUqGqZ1pN7DqZWiLQxo"
        )

        every { loginUseCase.execute(any()) } returns mockTokenPair

        // when & then
        mockMvc.perform(
            post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").exists())
            .andExpect(jsonPath("$.data.refreshToken").exists())
            .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
            .andDo(
                document(
                    "user-login",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("email")
                            .type(JsonFieldType.STRING)
                            .description("사용자 이메일 주소"),
                        fieldWithPath("password")
                            .type(JsonFieldType.STRING)
                            .description("비밀번호")
                    ),
                    responseFields(
                        fieldWithPath("success")
                            .type(JsonFieldType.BOOLEAN)
                            .description("API 처리 성공 여부"),
                        fieldWithPath("code")
                            .type(JsonFieldType.STRING)
                            .description("응답 코드"),
                        fieldWithPath("message")
                            .type(JsonFieldType.STRING)
                            .description("응답 메시지"),
                        fieldWithPath("data")
                            .type(JsonFieldType.OBJECT)
                            .description("응답 데이터"),
                        fieldWithPath("data.accessToken")
                            .type(JsonFieldType.STRING)
                            .description("액세스 토큰 (유효기간: 1시간)"),
                        fieldWithPath("data.refreshToken")
                            .type(JsonFieldType.STRING)
                            .description("리프레시 토큰 (유효기간: 7일)"),
                        fieldWithPath("data.tokenType")
                            .type(JsonFieldType.STRING)
                            .description("토큰 타입 (Bearer)"),
                        fieldWithPath("timestamp")
                            .type(JsonFieldType.STRING)
                            .description("응답 생성 시각 (ISO-8601 형식)")
                    )
                )
            )
    }

    @Test
    fun `로그인 실패 - 이메일 누락`() {
        // given
        val invalidRequest = mapOf(
            "password" to "Test1234!@#$"
        )

        // when & then
        mockMvc.perform(
            post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
            .andDo(
                document(
                    "user-login-fail-missing-email",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint())
                )
            )
    }
}
