using Entity.Model.Operational;
using Entity.Model.Security;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Entity.Dto.Operational
{
    public class ReviewTechnicalDto
    {
        public int Id { get; set; }
        public DateTime Date_review { get; set; }
        public string Code { get; set; }
        public string Observation { get; set; }
        public int FarmId { get; set; }
        public int TecnicoId { get; set; }
        public int ChecklistId { get; set; }
        public Boolean State { get; set; }
    }
}
