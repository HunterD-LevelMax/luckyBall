---
name: Lucky Ball Playful Toon
colors:
  surface: '#fff9ef'
  surface-dim: '#e1d9c7'
  surface-bright: '#fff9ef'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#fbf3e0'
  surface-container: '#f6edda'
  surface-container-high: '#f0e7d5'
  surface-container-highest: '#eae2cf'
  on-surface: '#1f1b10'
  on-surface-variant: '#4d4732'
  inverse-surface: '#343024'
  inverse-on-surface: '#f9f0dd'
  outline: '#7e775f'
  outline-variant: '#d0c6ab'
  surface-tint: '#705d00'
  primary: '#705d00'
  on-primary: '#ffffff'
  primary-container: '#ffd700'
  on-primary-container: '#705e00'
  inverse-primary: '#e9c400'
  secondary: '#0c6780'
  on-secondary: '#ffffff'
  secondary-container: '#9ae1ff'
  on-secondary-container: '#09657f'
  tertiary: '#ac2471'
  on-tertiary: '#ffffff'
  tertiary-container: '#ffccdf'
  on-tertiary-container: '#ad2571'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffe16d'
  primary-fixed-dim: '#e9c400'
  on-primary-fixed: '#221b00'
  on-primary-fixed-variant: '#544600'
  secondary-fixed: '#baeaff'
  secondary-fixed-dim: '#89d0ed'
  on-secondary-fixed: '#001f29'
  on-secondary-fixed-variant: '#004d62'
  tertiary-fixed: '#ffd8e6'
  tertiary-fixed-dim: '#ffb0d0'
  on-tertiary-fixed: '#3d0024'
  on-tertiary-fixed-variant: '#8c0058'
  background: '#fff9ef'
  on-background: '#1f1b10'
  surface-variant: '#eae2cf'
typography:
  display-xl:
    fontFamily: Plus Jakarta Sans
    fontSize: 48px
    fontWeight: '800'
    lineHeight: 52px
    letterSpacing: -0.02em
  display-xl-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 36px
    fontWeight: '800'
    lineHeight: 40px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '800'
    lineHeight: 36px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 28px
  body-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-md:
    fontFamily: Be Vietnam Pro
    fontSize: 16px
    fontWeight: '500'
    lineHeight: 22px
  label-bold:
    fontFamily: Rubik
    fontSize: 14px
    fontWeight: '700'
    lineHeight: 18px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  unit: 8px
  margin-mobile: 16px
  margin-desktop: 32px
  gutter: 12px
  stack-sm: 4px
  stack-md: 12px
  stack-lg: 24px
---

## Brand & Style
The design system is built on a "2D Flat Vector Toon" aesthetic, prioritizing high-energy, cheerful visuals that evoke the excitement of a casual mobile game. The target audience is broad, ranging from children to casual adult gamers, seeking a "pick-up-and-play" emotional response characterized by joy and accessibility.

The style is defined by:
- **Bold Outlines:** Every interactive element and container uses a thick, dark stroke to separate it from the background.
- **Flat Geometry:** No realistic textures or complex gradients; depth is achieved through offset "sticker" shadows and layering.
- **Hand-Drawn Energy:** Shapes are intentionally slightly "imperfect" or bouncy, avoiding clinical precision in favor of a playful, animated feel.

## Colors
This design system utilizes a high-saturation palette to maintain a high-energy "candy shop" feel. 

- **Primary (Sunny Yellow):** Used for the most critical actions, coin balances, and win states.
- **Secondary (Sky Blue):** Used for navigation backgrounds, world-map elements, and secondary buttons.
- **Tertiary (Candy Pink):** Reserved for special offers, hearts/lives, and "rare" UI notifications.
- **Grass Green:** Exclusively for "Go," "Start," or "Claim" buttons.
- **Bright Orange:** Used for leveling up, XP bars, and warnings.
- **Stroke/Outline:** A very dark chocolate brown (#2D1B08) is used instead of pure black for outlines to keep the palette warm and organic.

Dark mode is not supported; the interface must remain bright and high-contrast at all times.

## Typography
To replicate a bubbly, comic-book feel, the design system utilizes rounded, high-weight sans-serifs that mimic hand-lettered styles.

- **Headlines:** Use **Plus Jakarta Sans** with extra-bold weights. All headlines should feature a thick 4px-6px dark outline and a solid "drop-block" shadow (not a soft blur) offset by 4px down.
- **Body & Captions:** **Be Vietnam Pro** provides high readability while maintaining the friendly, rounded aesthetic.
- **Labels/Buttons:** **Rubik** is used for its geometric friendliness, ensuring text inside small buttons remains legible.

Text should often be slightly tilted (3-5 degrees) in UI banners to enhance the "hand-drawn" kinetic energy of the game.

## Layout & Spacing
The layout follows a "Fluid-Center" model. On mobile, elements are stacked vertically with generous margins to allow for large "thumb-friendly" hit targets.

- **Rhythm:** An 8px base grid is used, but spacing is often "optical" rather than strictly mathematical to preserve the hand-drawn feel.
- **Margins:** Large 16px safe areas on the sides of the screen prevent UI from hitting the bezel.
- **Grouping:** Use large, rounded containers to group related items (like a shop category or a player's stats), with at least 12px of internal padding.

## Elevation & Depth
Depth in this design system is purely "Graphic Depth," avoiding any realistic lighting or blurs.

- **Block Shadows:** Instead of Gaussian blurs, use solid color offsets. If a button is Green, its "shadow" is a darker shade of green (or the stroke color) offset 4px to 8px downwards.
- **Sticker Effect:** All primary UI panels should have a 4px white "outer border" outside of their dark stroke, making them look like stickers placed on a background.
- **No Glassmorphism:** Surfaces are 100% opaque. Do not use transparency for backgrounds as it reduces the "flat cartoon" impact.

## Shapes
Shapes are "Hyper-Rounded." 

- **Containers:** All panels use a minimum of 1rem (16px) corner radius. 
- **Buttons:** Interactive elements use the "Pill-shape" (3rem+) to look squishy and inviting.
- **Strokes:** All shapes must have a consistent 3pt to 5pt dark outline. Internal details (like the "inner shine" on a button) should also use rounded caps and joins.

## Components

- **Bubbly Buttons:** These are the centerpiece. Use a thick dark outline and a solid color block-shadow. When pressed, the button should shift down 4px (hiding the shadow) to simulate a physical "click." Use Grass Green for "Go" and Sky Blue for "Back."
- **Toon Cards:** Use rounded rectangles with the "Sticker Effect" (white outer border). The header of a card should typically "pop" out of the top edge of the card boundary.
- **Progress Bars:** Use a "well" (dark, recessed color) with a bright Orange or Green "fill" that has rounded ends. Add a small white "highlight" circle on the leading edge of the progress bar for a glossy-but-flat look.
- **Chips/Badges:** Small, pill-shaped markers for level numbers or item counts. These should always be high-contrast (e.g., White text on a Pink background).
- **Input Fields:** Thick-outlined rounded boxes with a slight inner-shadow (solid color) at the top to suggest they are "hollowed out" of the panel.
- **Modals:** Pop-ups should scale in with a "bounce" effect. They must have a large "X" button in the top-right corner that is circular, red, and has a heavy block shadow.