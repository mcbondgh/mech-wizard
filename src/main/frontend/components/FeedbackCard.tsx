import { CardElement } from "@vaadin/react-components";

export default function FeedbackCard({stars, customerName, response}: {stars: string, customerName: string, response: string}) {
    return (
        <section className="feedback-card-layout">
            <h4>{stars}</h4>
            <p>{customerName}</p>
            <span>{response}</span>
        </section>
    )
}