using System;

namespace OAuth2
{
	// represent an OAuth2 token
	public class OAuth2Token
	{
		/// <summary>
		/// Gets the token.
		/// </summary>
		/// <value>
		/// The token.
		/// </value>
		public string Token { get; set; }

		/// <summary>
		/// Gets or sets the Token expiration date.
		/// </summary>
		public DateTime? ExpirationDate { get; set; }

		/// <summary>
		/// Gets or sets the OAuth2 refresh token useful for getting a new access token once the old one expired.
		/// </summary>
		public string RefreshToken { get; set; }

		/// <summary>
		/// Gets or sets the user associated with the token.
		/// </summary>
		/// <value>
		/// The user.
		/// </value>
		public string UserName { get; set; }
	}
}
