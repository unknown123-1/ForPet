package com.forpet.app.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.forpet.app.core.designsystem.theme.ForPetTheme
import com.forpet.app.core.designsystem.theme.LocalForPetColors
import com.forpet.app.core.model.Pet
import com.forpet.app.core.designsystem.R as DesignSystemR

/**
 * PetProfileSection: 홈 화면 상단에 위치하는 펫 및 유저 프로필 요약 섹션입니다.
 * 펫이 등록된 경우: 펫 이름을 포함한 인사말 + 펫 사진
 * 미등록 상태: 기본 인사말 + 플레이스홀더(등록 유도 뱃지)
 *
 * SVG 기준:
 *  - 펫 이미지 원: cx=305 cy=144 r=40 → size=80dp
 *  - 카메라 뱃지: cx=335 cy=174 r=11.5 → size=23dp
 */
@Composable
internal fun PetProfileSection(
    pet: Pet? = null,
    onNavigateToRegister: () -> Unit = {},
    onNavigateToEdit: (petId: Long) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = LocalForPetColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Image(
            painter = painterResource(id = DesignSystemR.drawable.ic_logo),
            contentDescription = "포펫 로고",
        )

        Spacer(modifier = Modifier.height(17.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = "반가워요.",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    ),
                )
                Text(
                    text = if (pet != null) "${pet.name} 집사님!" else "집사님!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            }

            val showBadge = pet == null || pet.photoUri == null
            val badgeColor = if (pet == null) colors.primary else colors.inactive
            val badgeIcon = if (pet == null) Icons.Default.Add else Icons.Default.CameraAlt
            val badgeAction: () -> Unit = when {
                pet == null -> onNavigateToRegister
                else -> { { onNavigateToEdit(pet.id) } }
            }

            Box {
                if (pet?.photoUri != null) {
                    AsyncImage(
                        model = pet.photoUri,
                        contentDescription = "펫 프로필",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                    )
                } else {
                    Surface(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        color = colors.inactive,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(DesignSystemR.drawable.ic_pet_placeholder),
                                contentDescription = "펫 프로필",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(40.dp),
                            )
                        }
                    }
                }

                if (showBadge) {
                    Surface(
                        modifier = Modifier
                            .size(23.dp)
                            .align(Alignment.BottomEnd)
                            .border(2.dp, colors.background, CircleShape)
                            .clickable(onClick = badgeAction),
                        shape = CircleShape,
                        color = badgeColor,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = badgeIcon,
                                contentDescription = if (pet == null) "펫 등록" else "사진 추가",
                                tint = colors.onPrimary,
                                modifier = Modifier.size(13.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetProfileSectionNoPetPreview() {
    ForPetTheme {
        PetProfileSection(pet = null)
    }
}

@Preview(showBackground = true)
@Composable
private fun PetProfileSectionWithPetPreview() {
    ForPetTheme {
        PetProfileSection(
            pet = Pet(id = 1, name = "페퍼"),
        )
    }
}
