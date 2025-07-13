import { LitElement, html, css } from 'lit';
import { customElement } from 'lit/decorators.js';
import './FeedbackCard';

@customElement('feedback-card-example')
export class FeedbackCardExample extends LitElement {
  static styles = css`
    :host {
      display: block;
      padding: 20px;
      background-color: var(--page-body-color);
      min-height: 100vh;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
    }

    .header {
      text-align: center;
      margin-bottom: 30px;
    }

    .header h1 {
      color: var(--header-text-color);
      margin-bottom: 10px;
    }

    .header p {
      color: var(--subtitle-color);
      font-size: var(--lumo-font-size-l);
    }

    .feedback-cards-container {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
      gap: 20px;
    }

    .section-title {
      color: var(--header-text-color);
      font-size: var(--lumo-font-size-xl);
      margin: 30px 0 20px 0;
      padding-bottom: 10px;
      border-bottom: 2px solid var(--lumo-primary-color);
    }
  `;

  private sampleFeedbackData = [
    {
      id: '1',
      rating: 5,
      customerName: 'John Smith',
      carDetails: '2020 Toyota Camry - Engine Oil Change',
      customerFeedback: 'Excellent service! The mechanic was very professional and completed the work quickly. The car runs much smoother now. Highly recommend this service.',
      adminResponse: 'Thank you for your positive feedback, John! We\'re glad to hear that our service met your expectations. We look forward to serving you again.',
      date: '2024-01-15'
    },
    {
      id: '2',
      rating: 4,
      customerName: 'Sarah Johnson',
      carDetails: '2018 Honda Civic - Brake Pad Replacement',
      customerFeedback: 'Good service overall. The brake replacement was done well, but it took a bit longer than expected. The staff was friendly and kept me informed throughout the process.',
      adminResponse: 'Thank you for your feedback, Sarah. We apologize for the delay and appreciate your patience. We\'re working on improving our service times.',
      date: '2024-01-14'
    },
    {
      id: '3',
      rating: 5,
      customerName: 'Mike Davis',
      carDetails: '2021 Ford F-150 - Transmission Service',
      customerFeedback: 'Outstanding work! The transmission service was completed perfectly. The technician explained everything clearly and the price was fair. Will definitely return for future services.',
      adminResponse: 'We\'re thrilled to hear about your experience, Mike! Our team takes pride in providing quality service and clear communication. Thank you for choosing us!',
      date: '2024-01-13'
    },
    {
      id: '4',
      rating: 3,
      customerName: 'Emily Wilson',
      carDetails: '2019 Nissan Altima - Air Conditioning Repair',
      customerFeedback: 'The AC repair was completed, but I\'m not entirely satisfied with the result. The air isn\'t as cold as it should be. The staff was polite though.',
      adminResponse: 'We\'re sorry to hear about the AC issue, Emily. Please bring your car back so we can investigate and fix the problem properly. Your satisfaction is our priority.',
      date: '2024-01-12'
    },
    {
      id: '5',
      rating: 5,
      customerName: 'David Brown',
      carDetails: '2022 Chevrolet Silverado - Tire Rotation & Balance',
      customerFeedback: 'Perfect service! Quick, efficient, and professional. The tires feel great now and the ride is much smoother. Great value for money.',
      date: '2024-01-11'
    },
    {
      id: '6',
      rating: 4,
      customerName: 'Lisa Anderson',
      carDetails: '2020 BMW X3 - Battery Replacement',
      customerFeedback: 'Good service. The battery replacement was done quickly and the new battery works perfectly. The only minor issue was the waiting area could be more comfortable.',
      adminResponse: 'Thank you for your feedback, Lisa! We\'re glad the battery replacement went well. We\'ll look into improving the waiting area comfort for our customers.',
      date: '2024-01-10'
    }
  ];

  render() {
    return html`
      <div class="container">
        <div class="header">
          <h1>Customer Feedback Dashboard</h1>
          <p>Review and manage customer feedback for our auto repair services</p>
        </div>

        <h2 class="section-title">Recent Feedback</h2>
        <div class="feedback-cards-container">
          ${this.sampleFeedbackData.map(feedback => html`
            <feedback-card
              .feedback=${feedback}
              .showAdminResponse=${true}
            ></feedback-card>
          `)}
        </div>

        <h2 class="section-title">Feedback Without Admin Response</h2>
        <div class="feedback-cards-container">
          ${this.sampleFeedbackData.filter(f => !f.adminResponse).map(feedback => html`
            <feedback-card
              .feedback=${feedback}
              .showAdminResponse=${true}
            ></feedback-card>
          `)}
        </div>
      </div>
    `;
  }
}

declare global {
  interface HTMLElementTagNameMap {
    'feedback-card-example': FeedbackCardExample;
  }
}

export default FeedbackCardExample; 