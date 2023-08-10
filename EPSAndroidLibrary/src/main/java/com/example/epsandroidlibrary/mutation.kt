const val processPaymentMutation = """
    mutation(
        %s: String!
        %s: String!
        %s: String!
        %s: String!
        %s: String!
    ) {
        account {
            processPayment(
                accountNumber: $${'$'}$%s,
                sessionToken: $${'$'}$%s,
                totalAmount: $${'$'}$%s,
                subTotalAmount: $${'$'}$%s,
                taxes: $${'$'}$%s
            ) {
                data {
                    id
                    paymentDate
                    status
                    totalAmount {
                        units
                        value
                    }
                    statusReason
                    errorCode
                }
                error {
                    code
                    message
                }
            }
        }
    }
"""
