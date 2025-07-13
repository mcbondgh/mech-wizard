# Feedback Card Component

A reusable card component for displaying customer feedback with four main sections: rating stars, customer details, customer feedback, and admin response.

## Features

- **Four Organized Sections:**
  1. **Rating Stars** - Visual star rating (1-5 stars)
  2. **Customer Details** - Customer name and car information
  3. **Customer Feedback** - The actual feedback text
  4. **Admin Response** - Optional admin response (can be hidden)

- **Responsive Design** - Adapts to different screen sizes
- **Hover Effects** - Subtle animations on hover
- **Loading States** - Built-in loading state support
- **Empty States** - Handles cases with no data
- **Customizable** - Multiple props for customization

## Usage

### Basic Usage

```typescript
import './components/FeedbackCard';

// In your component
const feedbackData = {
  rating: 5,
  customerName: 'John Smith',
  carDetails: '2020 Toyota Camry - Engine Oil Change',
  customerFeedback: 'Excellent service! The mechanic was very professional.',
  adminResponse: 'Thank you for your feedback!',
  date: '2024-01-15',
  id: '1'
};

// In your template
html`
  <feedback-card .feedback=${feedbackData}></feedback-card>
`
```

### Advanced Usage

```typescript
html`
  <feedback-card
    .feedback=${feedbackData}
    .showAdminResponse=${false}
    .editable=${true}
  ></feedback-card>
`
```

## Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `feedback` | `FeedbackData` | `{}` | The feedback data object |
| `showAdminResponse` | `boolean` | `true` | Whether to show the admin response section |
| `editable` | `boolean` | `false` | Whether the card is editable (future feature) |

## FeedbackData Interface

```typescript
interface FeedbackData {
  rating: number;           // 1-5 star rating
  customerName: string;     // Customer's name
  carDetails: string;       // Car information and service
  customerFeedback: string; // Customer's feedback text
  adminResponse?: string;   // Optional admin response
  date?: string;           // Optional submission date
  id?: string;             // Optional unique identifier
}
```

## Styling

The component uses CSS custom properties that match the project's design system:

- `--lumo-primary-color` - Primary brand color
- `--header-text-color` - Text color for headers
- `--subtitle-color` - Color for secondary text
- `--item-layout-shadow` - Box shadow for cards
- `--lumo-font-size-*` - Font size variables
- `--lumo-line-height-*` - Line height variables

## CSS Classes

### Main Container
- `.feedback-card` - Main card container

### Sections
- `.section` - Individual section container
- `.section-header` - Section title styling

### Rating Section
- `.rating-section` - Rating container
- `.stars` - Stars container
- `.star` - Individual star styling
- `.star.empty` - Empty star styling
- `.rating-text` - Rating text styling

### Customer Details
- `.customer-details` - Customer info container
- `.customer-name` - Customer name styling
- `.car-details` - Car information styling
- `.car-icon` - Car icon styling

### Feedback Content
- `.feedback-content` - Feedback text container
- `.feedback-text` - Feedback text styling

### Admin Response
- `.admin-response` - Admin response container
- `.admin-response-header` - Admin response header
- `.admin-icon` - Admin icon styling
- `.admin-label` - Admin label styling
- `.admin-text` - Admin response text
- `.no-response` - No response message styling

### Utility Classes
- `.feedback-date` - Date styling
- `.feedback-cards-container` - Container for multiple cards
- `.loading` - Loading state styling
- `.empty` - Empty state styling

## Responsive Design

The component is fully responsive with breakpoints:

- **Desktop**: Cards display in a grid layout
- **Tablet**: Reduced padding and spacing
- **Mobile**: Single column layout with optimized spacing

## Examples

### Multiple Cards Layout

```typescript
html`
  <div class="feedback-cards-container">
    ${feedbackList.map(feedback => html`
      <feedback-card .feedback=${feedback}></feedback-card>
    `)}
  </div>
`
```

### Loading State

```typescript
html`
  <feedback-card 
    .feedback=${feedbackData}
    class="loading"
  ></feedback-card>
`
```

### Empty State

```typescript
html`
  <feedback-card 
    class="empty"
  >
    <div class="empty-message">No feedback available</div>
    <div class="empty-subtitle">Check back later for new feedback</div>
  </feedback-card>
`
```

## Browser Support

- Modern browsers with ES6+ support
- CSS Grid and Flexbox support required
- CSS custom properties support required

## Dependencies

- Lit (for component framework)
- No external dependencies required

## File Structure

```
src/main/frontend/
├── components/
│   ├── FeedbackCard.tsx          # Main component
│   ├── FeedbackCardExample.tsx   # Usage example
│   └── README.md                 # This documentation
└── themes/mechwizard/views/
    └── feedback-card-view.css    # Component styles
``` 