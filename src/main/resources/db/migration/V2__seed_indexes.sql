CREATE INDEX idx_currency_rate_pair_time ON currency_rate(quote_currency, fetched_at DESC);
CREATE INDEX idx_currency_rate_time ON currency_rate(fetched_at DESC);