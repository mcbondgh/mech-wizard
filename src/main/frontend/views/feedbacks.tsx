import React, { useState } from 'react';
import { Button } from '@vaadin/react-components';
import { useSignal } from '@vaadin/hilla-react-signals';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
 
export const config: ViewConfig = {
  menu: { order: 9, icon: 'line-awesome/svg/feedback.svg' },
  title: 'Feedbacks',
};

export default function FeedbackView() {
    const [getFeedbacks, setFeedbacks] = useState();
    const allFeedbacks = useSignal();

  return (
    <section className="feedback-view-parent-box">
      <section className="view-header-container ">
        <h1>Another view Here...</h1>
      </section>
    </section>
  );
}
