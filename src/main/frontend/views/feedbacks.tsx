import React, { useState } from 'react';

import { useSignal } from '@vaadin/hilla-react-signals';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button } from '@vaadin/react-components';
import { useNavigate } from 'react-router';
 
export const config: ViewConfig = {
  route: "views/feedbacks",
  // menu: { order: 9, icon: 'line-awesome/svg/feedback.svg' },
  title: 'Feedbacks',
  flowLayout: false
};

export default function FeedbackView() {
    const [getFeedbacks, setFeedbacks] = useState();
    const allFeedbacks = useSignal();
    const navigate = useNavigate();

    function backToDashboard()  {
      // @ts-ignore
      navigate("/dashboard");
    }

  return (
    <section className="feedback-view-parent-box box-border p-l">
      <section className="view-header-container">
        <header className="feedback-header m-l box-border border-s pl-m">
          <Button title="Click Me" onClick={backToDashboard} theme="primary success" className="w-auto">Button</Button>
          <h2>Feedback View</h2>
        </header>
      </section>
    </section>
  );
}//END OF
