import React, { useEffect, useState } from 'react';

import { useSignal } from '@vaadin/hilla-react-signals';
import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button } from '@vaadin/react-components';
import { useNavigate } from 'react-router';
import FeedbackCard from 'Frontend/components/FeedbackCard';



export const config: ViewConfig = {
  route: "hilla-route",
  // menu: { order: 9, icon: 'line-awesome/svg/feedback.svg' },
  title: 'Feedbacks',
  // rolesAllowed: ['admin', 'mechanic', 'receptionist'],
  flowLayout: false
};

export default function FeedbackView() {
    const [getFeedbacks, setFeedbacks] = useState();
    const allFeedbacks = useSignal();
    const navigate = useNavigate();
    const [getRole, setRole] = useState('');

    function backToDashboard()  {
      // @ts-ignore
      navigate("/dashboard");
    }

    //create an array of data i can iterate through
  let userResponse = [
    {
      "stars" : "5-⭐⭐⭐⭐⭐",
      "customerName" : "name",
      "response" : "This is a 5-star response"
    },
    {
      "stars" : "4-⭐⭐⭐⭐",
      "customerName" : "name",
      "response" : "This is a 4-star response"
    },
    {
      "stars" : "3-⭐⭐⭐",
      "customerName" : "name",
      "response" : "This is a 3-star response"
    },
    {
      "stars" : "2-⭐⭐",
      "customerName" : "name",
      "response" : "This is a 2-star response"
    },
    {
      "stars" : "1-⭐",
      "customerName" : "name",
      "response" : "This is a 1-star response"
    }
  ]


  return (
    <section className="feedback-view-parent-box box-border p-l">
      <section className="view-header-container">
        <header className="feedback-header m-l box-border border-s pl-m">
          <Button title="Click Me" onClick={backToDashboard} theme="primary success" className="w-auto">Button</Button>
          <h2>Feedback View</h2>
        </header>
      </section>
      <div className="cards-container">
        {userResponse.map(item => (
          <FeedbackCard
            stars={item.stars}
            customerName={item.customerName}
            response={item.response}
          />
        ))}
      </div>
    </section>
  );
}//END OF
