export interface ChatMessage {
  id?: number;
  sender: 'USER' | 'TUTOR';
  content: string;
  timestamp: string;
}

export interface Feedback {
  id?: number;
  userId: number;
  type: 'ERROR' | 'CONSOLIDATED';
  originalPhrase: string;
  content: string;
  explanation: string;
  timestamp: string;
}

export interface DidacticReport {
  summary: string;
  strengths: string[];
  weaknesses: string[];
  actionPlan: string;
}
