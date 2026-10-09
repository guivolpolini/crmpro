export interface GeneratePitchRequest {
  recipientName: string
  segment?: string
  painPoints?: string
  targetProductName?: string
}

export interface AiPitchResponse {
  subject: string
  pitchText: string
  callToAction: string
}
