package me.silvernine.jwttutorial.dto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Token 정보를 응답하기 위한 Dto
// Dto는 단순히 클라이언트에게 받아서 전송하는것 뿐 아니라 서버에서도 클라에게 전송할 때 사용하는 객체를 DTO라고 함.
public class TokenDto {
    private String token;
}
