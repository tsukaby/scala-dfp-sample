package com.tsukaby.dfp

import com.google.api.ads.common.lib.auth.OfflineCredentials
import com.google.api.ads.common.lib.auth.OfflineCredentials.Api
import com.google.api.ads.dfp.axis.factory.DfpServices
import com.google.api.ads.dfp.axis.utils.v201405.StatementBuilder
import com.google.api.ads.dfp.axis.v201405.UserServiceInterface
import com.google.api.ads.dfp.lib.client.DfpSession

/**
 * Created by tsukaby on 2014/07/09.
 */
object GetAllUsers {
  def runExample(dfpServices: DfpServices, session: DfpSession) {
    // Get the UserService.
    val userService: UserServiceInterface = dfpServices.get(session, classOf[UserServiceInterface])

    // Create a statement to get all users.
    val statementBuilder = new StatementBuilder().orderBy("id ASC").limit(StatementBuilder.SUGGESTED_PAGE_LIMIT)

    // Default for total result set size.
    var totalResultSetSize: Int = 0;

    do {
      // Get users by statement.
      val page = userService.getUsersByStatement(statementBuilder.toStatement());

      if (page.getResults() != null) {
        totalResultSetSize = page.getTotalResultSetSize()
        var i = page.getStartIndex()
        for (user <- page.getResults()) {
          i += 1
          printf("%d) User with ID \"%d\" and name \"%s\" was found.\n", i, user.getId(), user.getName())
        }
      }

      statementBuilder.increaseOffsetBy(StatementBuilder.SUGGESTED_PAGE_LIMIT);
    } while (statementBuilder.getOffset() < totalResultSetSize);

    printf("Number of results found: %d\n", totalResultSetSize)
  }

  def main(args: Array[String]) {
    // Generate a refreshable OAuth2 credential similar to a ClientLogin token
    // and can be used in place of a service account.
    val oAuth2Credential = new OfflineCredentials.Builder().forApi(Api.DFP).fromFile().build().generateCredential()

    // Construct a DfpSession.
    val session = new DfpSession.Builder()
      .fromFile()
      .withOAuth2Credential(oAuth2Credential)
      .build()

    val dfpServices = new DfpServices()

    runExample(dfpServices, session)
  }
}
