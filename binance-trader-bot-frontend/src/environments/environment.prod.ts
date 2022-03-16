import { Environment } from 'src/app/types/environment';

export const environment: Environment = {
  production: true,
  
  // same-origin call in production
  apiBaseUrl: '/api',
  
  fetchRequestMode: 'same-origin',
};
