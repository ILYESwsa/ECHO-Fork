package iad1tya.echo.music.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

// ═══════════════════════════════════════════════════════════════════
//  LIQUID GLASS DESIGN SYSTEM  ·  Echo Music
//
//  All glass surfaces are built from three visual layers:
//   1. Background blur  — blurred album-art or wallpaper behind the panel
//   2. Translucent fill — semi-opaque dark/light overlay
//   3. Specular border  — thin gradient stroke faking light reflection
// ═══════════════════════════════════════════════════════════════════

// ─── Token constants ────────────────────────────────────────────────

/** Background scrim alpha used by full-screen blur backgrounds. */
const val GLASS_SCRIM_ALPHA       = 0.48f

/** Panel fill alpha for primary glass cards (player controls, cards). */
const val GLASS_FILL_ALPHA        = 0.18f

/** Panel fill alpha for secondary / smaller surfaces (nav bar, chips). */
const val GLASS_FILL_ALPHA_LIGHT  = 0.13f

/** Panel fill alpha for elevated/popup surfaces (menus, dialogs). */
const val GLASS_FILL_ALPHA_HIGH   = 0.24f

/** Specular border opacity on the bright (top-left) edge. */
const val GLASS_BORDER_ALPHA      = 0.45f

/** Specular border opacity on the dim (bottom-right) edge. */
const val GLASS_BORDER_ALPHA_DIM  = 0.10f

/** Inner highlight strip at the very top of a glass card. */
const val GLASS_HIGHLIGHT_ALPHA   = 0.12f

/** Blur radius for standard glass panels sitting on a blurred bg. */
val GLASS_BLUR_RADIUS_PANEL: Dp   = 0.dp   // panels don't self-blur (bg already blurred)

/** Blur radius applied to the full-screen background wallpaper layer. */
val GLASS_BLUR_RADIUS_BG: Dp      = 32.dp

/** Corner radius for large cards (player controls panel, dialogs). */
val GLASS_CORNER_LARGE: Dp        = 28.dp

/** Corner radius for medium cards (list items, chips). */
val GLASS_CORNER_MEDIUM: Dp       = 20.dp

/** Corner radius for small surfaces (buttons, mini player). */
val GLASS_CORNER_SMALL: Dp        = 16.dp

/** Corner radius for pill shapes (nav bar, search bar). */
val GLASS_CORNER_PILL: Dp         = 100.dp

// ─── Glass fill colors ──────────────────────────────────────────────

/** Semi-transparent dark fill for glass panels on dark backgrounds. */
val GlassFillDark   = Color(0x2E000000)   // ~18 % black

/** Semi-transparent light fill for glass panels on light backgrounds. */
val GlassFillLight  = Color(0x28FFFFFF)   // ~16 % white

/** Richer fill used for elevated / popup surfaces. */
val GlassFillElevated = Color(0x3D000000) // ~24 % black

// ─── Composition local ─────────────────────────────────────────────

/** True when a full-screen blur background is active behind the UI,
 *  allowing child composables to skip their own blur and rely on the
 *  ambient background instead. */
val LocalGlassBackgroundActive = staticCompositionLocalOf { false }

// ─── Modifier extensions ────────────────────────────────────────────

/**
 * Primary Liquid Glass card modifier.
 *
 * Clips the composable to [shape], applies a semi-transparent [fillColor],
 * and draws a specular gradient border that simulates light hitting glass.
 *
 * Usage:
 * ```
 * Box(modifier = Modifier
 *     .fillMaxWidth()
 *     .liquidGlass()
 * ) { … }
 * ```
 */
fun Modifier.liquidGlass(
    shape: Shape           = RoundedCornerShape(GLASS_CORNER_LARGE),
    fillColor: Color       = GlassFillDark,
    borderAlpha: Float     = GLASS_BORDER_ALPHA,
    borderAlphaDim: Float  = GLASS_BORDER_ALPHA_DIM,
    borderWidth: Dp        = 1.dp,
): Modifier = this
    .clip(shape)
    .background(fillColor, shape)
    .border(
        width = borderWidth,
        brush = Brush.linearGradient(
            0.00f to Color.White.copy(alpha = borderAlpha),
            0.40f to Color.White.copy(alpha = borderAlphaDim),
            0.60f to Color.White.copy(alpha = borderAlphaDim * 0.5f),
            1.00f to Color.White.copy(alpha = borderAlpha * 0.6f),
        ),
        shape = shape,
    )

/**
 * Lighter glass surface for secondary / smaller elements
 * (chips, nav items, list row active states).
 */
fun Modifier.liquidGlassLight(
    shape: Shape        = RoundedCornerShape(GLASS_CORNER_MEDIUM),
    fillAlpha: Float    = GLASS_FILL_ALPHA_LIGHT,
    borderAlpha: Float  = GLASS_BORDER_ALPHA * 0.7f,
): Modifier = this.liquidGlass(
    shape        = shape,
    fillColor    = Color.White.copy(alpha = fillAlpha),
    borderAlpha  = borderAlpha,
    borderWidth  = 0.8.dp,
)

/**
 * Elevated glass surface for modals, dialogs, bottom sheets.
 */
fun Modifier.liquidGlassElevated(
    shape: Shape = RoundedCornerShape(GLASS_CORNER_LARGE),
): Modifier = this.liquidGlass(
    shape     = shape,
    fillColor = GlassFillElevated,
    borderAlpha = GLASS_BORDER_ALPHA * 1.1f,
)

/**
 * Pill-shaped glass surface (navigation bar, search bar, mini-player).
 */
fun Modifier.liquidGlassPill(
    fillColor: Color   = GlassFillDark,
    borderAlpha: Float = GLASS_BORDER_ALPHA,
): Modifier = this.liquidGlass(
    shape       = RoundedCornerShape(GLASS_CORNER_PILL),
    fillColor   = fillColor,
    borderAlpha = borderAlpha,
)

// ─── Full-screen background layer ───────────────────────────────────

/**
 * A full-width, full-height blurred background composable.
 *
 * Place this as the first child of a [Box] that fills the screen.
 * Child glass panels will appear to float on the frosted surface.
 *
 * @param blurRadius   Blur strength. Default [GLASS_BLUR_RADIUS_BG] (32 dp).
 * @param scrimAlpha   Dark overlay opacity applied on top of the blur.
 * @param content      The blurred content (e.g. AsyncImage with album art).
 */
@Composable
fun GlassBlurBackground(
    blurRadius: Dp   = GLASS_BLUR_RADIUS_BG,
    scrimAlpha: Float = GLASS_SCRIM_ALPHA,
    content: @Composable () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 1 — blurred content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = blurRadius)
        ) {
            content()
        }
        // Layer 2 — dark scrim for contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = scrimAlpha))
        )
        // Layer 3 — subtle vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.Transparent,
                        0.55f to Color.Black.copy(alpha = 0.15f),
                        1.00f to Color.Black.copy(alpha = 0.45f),
                    )
                )
        )
    }
}

// ─── Convenience color helpers ──────────────────────────────────────

/** Content (text / icon) color on any dark glass surface. */
val GlassContentColorOnDark  = Color.White

/** Content (text / icon) color on any light glass surface. */
val GlassContentColorOnLight = Color(0xFF1A1A1A)

/** Muted / secondary content color on dark glass. */
val GlassContentColorMuted   = Color.White.copy(alpha = 0.70f)

/** Separator / divider color that works on glass surfaces. */
val GlassDividerColor        = Color.White.copy(alpha = 0.12f)

/** Active / selected indicator tint on glass nav items. */
val GlassSelectedTint        = Color.White.copy(alpha = 0.22f)
