using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Model.Operational
{
    public class ReviewEvidence
    {
        public int Id { get; set; }
        public int? EvidenceId { get; set; }
        public Evidence? Evidence { get; set; }
        public int? ReviewId { get; set; }
        public ReviewTechnical? Review { get; set; }
        public Boolean State { get; set; }
        public DateTime Created_at { get; set; }
        public DateTime? Updated_at { get; set; }
        public DateTime? Deleted_at { get; set; }
    }
}
