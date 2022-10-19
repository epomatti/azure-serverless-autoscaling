param location string = resourceGroup().location

param serviceBusNamespaceName string = 'bus-serverless-bookstore-dev'

// Namespace
resource serviceBusNamespace 'Microsoft.ServiceBus/namespaces@2022-01-01-preview' = {
  name: serviceBusNamespaceName
  location: location
  sku: {
    name: 'Basic'
  }
}

// Queues

resource serviceBusQueueInvoiceCreate 'Microsoft.ServiceBus/namespaces/queues@2022-01-01-preview' = {
  parent: serviceBusNamespace
  name: 'invoice-create'
  properties: properties
}

resource serviceBusQueueInvoiceAuthorize 'Microsoft.ServiceBus/namespaces/queues@2022-01-01-preview' = {
  parent: serviceBusNamespace
  name: 'invoice-authorize'
  properties: properties
}

resource serviceBusQueueInvoiceAuthorized 'Microsoft.ServiceBus/namespaces/queues@2022-01-01-preview' = {
  parent: serviceBusNamespace
  name: 'invoice-authorized'
  properties: properties
}

// Shared

var properties = {
  requiresDuplicateDetection: false
  requiresSession: false
  deadLetteringOnMessageExpiration: false
  maxDeliveryCount: 3
  enablePartitioning: true
}
